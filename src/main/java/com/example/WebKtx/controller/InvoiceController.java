package com.example.WebKtx.controller;

import com.example.WebKtx.anotation.ApiMessage;
import com.example.WebKtx.dto.InvoiceDto.InvoiceCreateRequest;
import com.example.WebKtx.dto.InvoiceDto.InvoiceUpdateRequest;
import com.example.WebKtx.dto.RoomServiceDetailDto.ServiceDetailItem;
import com.example.WebKtx.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webktx/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @PostMapping("/create")
    @ApiMessage("Create invoice success")
    public ResponseEntity<?> create(@Valid @RequestBody InvoiceCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update invoice success")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody InvoiceUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete invoice success")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok("Delete complete");
    }

    @GetMapping("/{id}")
    @ApiMessage("Get invoice success")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @ApiMessage("Get invoices (paged) success")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int pageIndex,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(service.getAll(PageRequest.of(pageIndex, pageSize)));
    }

    // detail lines
    @PostMapping("/{id}/details")
    @ApiMessage("Add invoice line success")
    public ResponseEntity<?> addDetail(@PathVariable String id, @RequestBody ServiceDetailItem item) {
        return ResponseEntity.ok(service.addDetail(id, item));
    }

    @DeleteMapping("/{id}/details/{detailId}")
    @ApiMessage("Remove invoice line success")
    public ResponseEntity<?> removeDetail(@PathVariable String id, @PathVariable String detailId) {
        return ResponseEntity.ok(service.removeDetail(id, detailId));
    }

    // recompute totals
    @PutMapping("/{id}/recompute")
    @ApiMessage("Recompute invoice total success")
    public ResponseEntity<?> recompute(@PathVariable String id) {
        return ResponseEntity.ok(service.recompute(id));
    }

    // mark paid if enough payments
    @PutMapping("/{id}/mark-paid-if-enough")
    @ApiMessage("Mark invoice paid if enough success")
    public ResponseEntity<?> markPaidIfEnough(@PathVariable String id) {
        return ResponseEntity.ok(service.markPaidIfEnough(id));
    }
}

