package com.example.WebKtx.repository;

import com.example.WebKtx.entity.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, String> {
    @Query("select sd from ServiceDetail sd join fetch sd.service where sd.invoice.id = :invoiceId")
    List<ServiceDetail> findAllByInvoiceIdFetchService(@Param("invoiceId") String invoiceId);

    void deleteAllByInvoiceId(String invoiceId);
}