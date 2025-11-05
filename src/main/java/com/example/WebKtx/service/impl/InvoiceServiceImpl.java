package com.example.WebKtx.service.impl;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.dto.InvoiceDto.InvoiceCreateRequest;
import com.example.WebKtx.dto.InvoiceDto.InvoiceResponse;
import com.example.WebKtx.dto.InvoiceDto.InvoiceSummaryResponse;
import com.example.WebKtx.dto.InvoiceDto.InvoiceUpdateRequest;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomServiceDetailDto.ServiceDetailItem;
import com.example.WebKtx.entity.*;
import com.example.WebKtx.mapper.InvoiceMapper;
import com.example.WebKtx.repository.*;
import com.example.WebKtx.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.example.WebKtx.common.Util.PaginationUtils.wrap;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final ServiceDetailRepository detailRepo;
    private final RoomServiceRepository roomServiceRepo;
    private final PaymentRepository paymentRepo;
    private final RoomRepository roomRepo;
    private final InvoiceMapper mapper;

    @Override
    public InvoiceResponse create(InvoiceCreateRequest req) {
        // Chuẩn hóa month về ngày 1
        LocalDate month = req.getMonth().withDayOfMonth(1);

        // Unique (room, month)
        invoiceRepo.findByRoomAndMonth(req.getRoomId(), month).ifPresent(x -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice already exists for room/month");
        });

        Room room = roomRepo.findById(req.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "room not found"));

        // Room fee lấy từ req hoặc room.price
        BigDecimal roomFee = nonNullNonNegative(req.getRoomFee(), room.getPrice(), "roomFee");
        Invoice inv = Invoice.builder()
                .room(room)
                .month(month)
                .createdAt(LocalDate.now())
                .roomFee(roomFee)
                .totalServiceFee(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .status(InvoiceStatus.UNPAID)
                .build();

        inv = invoiceRepo.save(inv);

        // Add các dòng chi tiết
        if (req.getDetails() != null && !req.getDetails().isEmpty()) {
            for (ServiceDetailItem it : req.getDetails()) {
                addDetailLine(inv, it);
            }
        }

        // Tính tổng
        recomputeInternal(inv.getId());

        return loadFull(inv.getId());
    }

    @Override
    public InvoiceResponse update(String id, InvoiceUpdateRequest req) {
        Invoice inv = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (req.getRoomFee() != null) {
            inv.setRoomFee(nonNegative(req.getRoomFee(), "roomFee"));
        }
        if (req.getStatus() != null) {
            inv.setStatus(req.getStatus());
        }

        // replace toàn bộ details nếu truyền vào
        if (req.getDetails() != null) {
            detailRepo.deleteAllByInvoiceId(inv.getId());
            for (ServiceDetailItem it : req.getDetails()) {
                addDetailLine(inv, it);
            }
        }

        recomputeInternal(inv.getId());
        return loadFull(inv.getId());
    }

    @Override
    public void delete(String id) {
        if (!invoiceRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        invoiceRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse findById(String id) {
        return loadFull(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAll(Pageable pageable) {
        Page<Invoice> page = invoiceRepo.findAll(pageable);
        List<InvoiceSummaryResponse> items = page.getContent().stream()
                .map(mapper::toSummary)
                .toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(items);
        return dto;
    }

    @Override
    public InvoiceResponse addDetail(String invoiceId, ServiceDetailItem item) {
        Invoice inv = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        addDetailLine(inv, item);
        recomputeInternal(invoiceId);
        return loadFull(invoiceId);
    }

    @Override
    public InvoiceResponse removeDetail(String invoiceId, String detailId) {
        invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        detailRepo.deleteById(detailId);
        recomputeInternal(invoiceId);
        return loadFull(invoiceId);
    }

    @Override
    public InvoiceResponse recompute(String id) {
        recomputeInternal(id);
        return loadFull(id);
    }

    @Override
    public InvoiceResponse markPaidIfEnough(String id) {
        Invoice inv = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // đảm bảo total đúng trước khi so sánh
        recomputeInternal(id);

        BigDecimal paid = paymentRepo.sumSuccessAmount(id);
        if (paid.compareTo(inv.getTotalAmount()) >= 0) {
            inv.setStatus(InvoiceStatus.PAID);
            invoiceRepo.save(inv);
        }
        return loadFull(id);
    }

    // ================== Helpers ==================

    private void addDetailLine(Invoice inv, ServiceDetailItem it) {
        RoomService svc = resolveService(it);

        BigDecimal qty = nonNegative(defaultZero(it.getQuantity()), "quantity");
        BigDecimal unitPrice = nonNegative(
                it.getUnitPrice() != null ? it.getUnitPrice() : defaultZero(svc.getPrice()),
                "unitPrice"
        );

        ServiceDetail sd = ServiceDetail.builder()
                .invoice(inv)
                .service(svc)
                .quantity(qty)
                .unitPrice(unitPrice)
                .unit(it.getUnit())
                .description(it.getDescription())
                .totalAmount(unitPrice.multiply(qty))
                .build();

        detailRepo.save(sd);
    }

    private RoomService resolveService(ServiceDetailItem it) {
        if (it.getServiceId() != null) {
            return roomServiceRepo.findById(it.getServiceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "service not found"));
        }
        if (it.getServiceName() != null) {
            return roomServiceRepo.findByNameIgnoreCase(it.getServiceName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "service name not found"));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serviceId or serviceName required");
    }

    private void recomputeInternal(String invoiceId) {
        Invoice inv = invoiceRepo.findWithDetailsById(invoiceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<ServiceDetail> lines = detailRepo.findAllByInvoiceIdFetchService(invoiceId);

        BigDecimal svcTotal = lines.stream()
                .map(ServiceDetail::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        inv.setTotalServiceFee(svcTotal);
        inv.setTotalAmount(nonNegative(inv.getRoomFee(), "roomFee").add(svcTotal));
        invoiceRepo.save(inv);
    }

    private InvoiceResponse loadFull(String id) {
        Invoice inv = invoiceRepo.findWithDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<ServiceDetail> lines = detailRepo.findAllByInvoiceIdFetchService(id);
        return mapper.toResponse(inv, lines);
    }

    // ===== validate helpers =====
    private BigDecimal nonNullNonNegative(BigDecimal prefer, BigDecimal fallback, String field) {
        BigDecimal v = prefer != null ? prefer : defaultZero(fallback);
        return nonNegative(v, field);
    }

    private BigDecimal nonNegative(BigDecimal v, String field) {
        if (v == null) v = BigDecimal.ZERO;
        if (v.signum() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must be >= 0");
        return v;
    }

    private BigDecimal defaultZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ===== NEW: Search =====
    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByMonth(LocalDate month, Pageable pageable) {
        Page<Invoice> page = invoiceRepo.findByMonth(month, pageable);
        List<InvoiceSummaryResponse> items = page.getContent().stream()
                .map(mapper::toSummary)
                .toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(items);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByRoom(String roomId, Pageable pageable) {
        Page<Invoice> page = invoiceRepo.findByRoomId(roomId, pageable);
        List<InvoiceSummaryResponse> items = page.getContent().stream()
                .map(mapper::toSummary)
                .toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(items);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO searchByStatus(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> page = invoiceRepo.findByStatus(status, pageable);
        List<InvoiceSummaryResponse> items = page.getContent().stream()
                .map(mapper::toSummary)
                .toList();

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setMeta(meta);
        dto.setResult(items);
        return dto;
    }

    @Override
    public InvoiceResponse findLatestUnpaidByRoom(String roomId) {
        Invoice inv = invoiceRepo
                .findFirstByRoom_IdAndStatusOrderByMonthDesc(roomId, InvoiceStatus.UNPAID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No UNPAID invoice for this room"));
        List<ServiceDetail> lines = inv.getServiceDetails() == null
                ? List.of()
                : inv.getServiceDetails().stream().toList();

        return mapper.toResponse(inv, lines);
    }
}

