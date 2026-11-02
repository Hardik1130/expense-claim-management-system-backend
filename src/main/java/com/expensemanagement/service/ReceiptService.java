package com.expensemanagement.service;

import com.expensemanagement.dto.response.ReceiptResponse;
import com.expensemanagement.service.impl.ReceiptServiceImpl.ReceiptFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface ReceiptService {
    ReceiptResponse upload(UUID userId, UUID claimId, MultipartFile file);

    List<ReceiptResponse> list(UUID userId, UUID claimId, boolean manager);

    ReceiptFile download(UUID userId, UUID receiptId, boolean manager);

    void delete(UUID userId, UUID claimId);
}
