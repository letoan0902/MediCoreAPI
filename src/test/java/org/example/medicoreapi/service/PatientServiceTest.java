package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.PatientRequest;
import org.example.medicoreapi.dto.response.PatientResponse;
import org.example.medicoreapi.entity.Patient;
import org.example.medicoreapi.exception.ResourceNotFoundException;
import org.example.medicoreapi.repository.AppointmentRepository;
import org.example.medicoreapi.repository.DoctorRepository;
import org.example.medicoreapi.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PatientService patientService;

    // Test tạo mới bệnh nhân thành công
    @Test
    void createPatient_success() {
        PatientRequest request = new PatientRequest();
        request.setFullName("Nguyen Van A");
        request.setDateOfBirth(LocalDate.of(2003, 5, 18));
        request.setGender("Male");
        request.setPhone("0987654321");
        request.setAddress("Ha Noi");

        Patient savedPatient = Patient.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .dateOfBirth(LocalDate.of(2003, 5, 18))
                .gender("Male")
                .phone("0987654321")
                .address("Ha Noi")
                .build();

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientResponse response = patientService.createPatient(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyen Van A", response.getFullName());
        assertEquals("0987654321", response.getPhone());

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    // Test lấy danh sách tất cả bệnh nhân
    @Test
    void getAllPatients_success() {
        Patient patient1 = Patient.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .phone("111")
                .build();

        Patient patient2 = Patient.builder()
                .id(2L)
                .fullName("Tran Thi B")
                .phone("222")
                .build();

        when(patientRepository.findAll()).thenReturn(List.of(patient1, patient2));

        List<PatientResponse> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        assertEquals("Nguyen Van A", result.get(0).getFullName());
        assertEquals("Tran Thi B", result.get(1).getFullName());

        verify(patientRepository, times(1)).findAll();
    }

    // Test lấy bệnh nhân theo id thành công
    @Test
    void getPatientById_success() {
        Patient patient = Patient.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .phone("0987654321")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponse response = patientService.getPatientById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyen Van A", response.getFullName());

        verify(patientRepository, times(1)).findById(1L);
    }

    // Test lấy bệnh nhân theo id nhưng không tồn tại
    @Test
    void getPatientById_notFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientById(99L);
        });

        verify(patientRepository, times(1)).findById(99L);
    }

    // Test cập nhật thông tin bệnh nhân thành công
    @Test
    void updatePatient_success() {
        Patient existingPatient = Patient.builder()
                .id(1L)
                .fullName("Old Name")
                .phone("111")
                .build();

        PatientRequest request = new PatientRequest();
        request.setFullName("New Name");
        request.setDateOfBirth(LocalDate.of(2003, 5, 18));
        request.setGender("Male");
        request.setPhone("222");
        request.setAddress("Ha Noi");

        Patient updatedPatient = Patient.builder()
                .id(1L)
                .fullName("New Name")
                .dateOfBirth(LocalDate.of(2003, 5, 18))
                .gender("Male")
                .phone("222")
                .address("Ha Noi")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        PatientResponse response = patientService.updatePatient(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("New Name", response.getFullName());
        assertEquals("222", response.getPhone());

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    // Test cập nhật bệnh nhân nhưng id không tồn tại
    @Test
    void updatePatient_notFound() {
        PatientRequest request = new PatientRequest();
        request.setFullName("New Name");

        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.updatePatient(99L, request);
        });

        verify(patientRepository, times(1)).findById(99L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    // Test xóa bệnh nhân thành công
    @Test
    void deletePatient_success() {
        Patient patient = Patient.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).delete(patient);
    }

    // Test xóa bệnh nhân nhưng id không tồn tại
    @Test
    void deletePatient_notFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(99L);
        });

        verify(patientRepository, times(1)).findById(99L);
        verify(patientRepository, never()).delete(any(Patient.class));
    }
}