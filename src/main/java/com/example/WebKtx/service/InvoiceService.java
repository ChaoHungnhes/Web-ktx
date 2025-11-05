package com.example.WebKtx.service;

import com.example.WebKtx.common.Enum.InvoiceStatus;
import com.example.WebKtx.dto.InvoiceDto.InvoiceCreateRequest;
import com.example.WebKtx.dto.InvoiceDto.InvoiceResponse;
import com.example.WebKtx.dto.InvoiceDto.InvoiceUpdateRequest;
import com.example.WebKtx.dto.ResultPaginationDTO;
import com.example.WebKtx.dto.RoomServiceDetailDto.ServiceDetailItem;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface InvoiceService {
    InvoiceResponse create(InvoiceCreateRequest req);            // generate 1 invoice (room+month)
    InvoiceResponse update(String id, InvoiceUpdateRequest req); // replace roomFee/details/status (optional)
    void delete(String id);
    InvoiceResponse findById(String id);
    ResultPaginationDTO getAll(Pageable pageable);

    // thêm/chỉnh từng dòng chi tiết
    InvoiceResponse addDetail(String invoiceId, ServiceDetailItem item);
    InvoiceResponse removeDetail(String invoiceId, String detailId);

    // đối soát lại tổng
    InvoiceResponse recompute(String id);

    // thanh toán nhanh (tuỳ chọn)
    InvoiceResponse markPaidIfEnough(String id);

    // ===== NEW: Search =====
    ResultPaginationDTO searchByMonth(LocalDate month, Pageable pageable);
    ResultPaginationDTO searchByRoom(String roomId, Pageable pageable);
    ResultPaginationDTO searchByStatus(InvoiceStatus status, Pageable pageable);
    InvoiceResponse findLatestUnpaidByRoom(String roomId);
}

