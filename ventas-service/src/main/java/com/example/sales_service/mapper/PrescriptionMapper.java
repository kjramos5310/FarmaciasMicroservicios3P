package com.example.sales_service.mapper;

import com.example.sales_service.dto.PrescriptionRequest;
import com.example.sales_service.entity.Customer;
import com.example.sales_service.entity.Prescription;
import com.example.sales_service.enums.PrescriptionStatus;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {
    
    public Prescription toEntity(PrescriptionRequest request, Customer customer) {
        if (request == null) {
            return null;
        }
        
        Prescription prescription = new Prescription();
        prescription.setCustomer(customer);
        prescription.setDoctorName(request.getDoctorName());
        prescription.setDoctorLicense(request.getDoctorLicense());
        prescription.setDoctorSpecialty(request.getDoctorSpecialty());
        prescription.setIssueDate(request.getIssueDate());
        prescription.setExpirationDate(request.getExpirationDate());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setNotes(request.getNotes());
        prescription.setStatus(request.getStatus() != null ? request.getStatus() : PrescriptionStatus.ACTIVE);
        
        return prescription;
    }
}
