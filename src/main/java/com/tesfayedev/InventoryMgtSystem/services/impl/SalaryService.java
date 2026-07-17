package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.tesfayedev.InventoryMgtSystem.enums.DeliveryStatus;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SalaryService {
    private static final BigDecimal THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal RATE_PER_THRESHOLD = new BigDecimal("300");

    public BigDecimal calculateCommission(Transaction txn){
        if(txn.getDeliveryStatus() != DeliveryStatus.DELIVERED || txn.getTotalPrice()==null){
            return BigDecimal.ZERO;
        }

        //Bucketed logic: floor(totalAmount / 50000)*300
        //A 75000 UGX total yields exactly 300 UGX in commission.
        BigDecimal multiples = txn.getTotalPrice()
                .divide(THRESHOLD,0,RoundingMode.FLOOR);

        return  multiples.multiply(RATE_PER_THRESHOLD);
    }
}
