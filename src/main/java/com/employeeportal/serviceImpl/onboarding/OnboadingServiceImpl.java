package com.employeeportal.serviceImpl.onboarding;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.employeeportal.config.EmailConstant;
import com.employeeportal.dto.onboarding.AadharCardDetailsDTO;
import com.employeeportal.dto.onboarding.AddressDTO;
import com.employeeportal.dto.onboarding.EducationDTO;
import com.employeeportal.dto.onboarding.EmployeeDTO;
import com.employeeportal.dto.onboarding.OnboardingResponseDTO;
import com.employeeportal.dto.onboarding.AdditionalDetailsDTO;
import com.employeeportal.dto.onboarding.PanCardDetailsDTO;
import com.employeeportal.dto.onboarding.PassportDetailsDTO;
import com.employeeportal.dto.onboarding.PersonalDetailsDTO;
import com.employeeportal.dto.onboarding.PreviewDto;
import com.employeeportal.dto.onboarding.PreviewResponseDTO;
import com.employeeportal.dto.onboarding.EmploymentHistoryDTO;
import com.employeeportal.dto.onboarding.GeneralResponse;
import com.employeeportal.dto.onboarding.ProfessionalReferencesDTO;
import com.employeeportal.dto.onboarding.RelativesDTO;
import com.employeeportal.dto.onboarding.VisaDetailsDTO;
import com.employeeportal.exception.BadRequestException;
import com.employeeportal.exception.FieldsMissingException;
import com.employeeportal.exception.NotFoundException;
import com.employeeportal.model.onboarding.Address;
import com.employeeportal.model.onboarding.Education;
import com.employeeportal.model.onboarding.IdentificationDetails;
import com.employeeportal.model.onboarding.IdentityType;
import com.employeeportal.model.onboarding.OnboardingDetails;
import com.employeeportal.model.onboarding.AdditionalDetails;
import com.employeeportal.model.onboarding.PassportDetails;
import com.employeeportal.model.onboarding.PersonalDetails;
import com.employeeportal.model.onboarding.PreviewDetails;
import com.employeeportal.model.onboarding.EmploymentHistory;
import com.employeeportal.model.onboarding.ProfessionalReferences;
import com.employeeportal.model.onboarding.Relatives;
import com.employeeportal.model.onboarding.VisaDetails;
import com.employeeportal.model.registration.Employee;
import com.employeeportal.repository.onboarding.AddressRepository;
import com.employeeportal.repository.onboarding.EducationRepository;
import com.employeeportal.repository.onboarding.EmploymentHistoryRepository;
import com.employeeportal.repository.onboarding.IdentificationDetailsRepository;
import com.employeeportal.repository.onboarding.OtherDetailsRepository;
import com.employeeportal.repository.onboarding.PassportDetailsRepository;
import com.employeeportal.repository.onboarding.PersonalDetailsRepository;
import com.employeeportal.repository.onboarding.PreviewRepository;
import com.employeeportal.repository.onboarding.ProfessionalReferencesRepository;
import com.employeeportal.repository.onboarding.RelativesRepository;
import com.employeeportal.repository.onboarding.VisaDetailsRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.onboarding.OnboardingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OnboadingServiceImpl implements OnboardingService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    private IdentificationDetailsRepository identificationDetailsRepository;

    @Autowired
    private OtherDetailsRepository otherDetailsRepository;

    @Autowired
    private PassportDetailsRepository passportDetailsRepository;

    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;

    @Autowired
    private ProfessionalReferencesRepository professionalReferencesRepository;

    @Autowired
    private RelativesRepository relativesRepository;

    @Autowired
    private VisaDetailsRepository visaDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PreviewRepository previewRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final long ONBOARDING_OBJ_EXPIRATION_TIME = 5; // 2 minutes

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public OnboardingResponseDTO fillOnboardingDetails(OnboardingDetails onboardingDetails, String email,
            String pageIdentifier) {

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);
        if (!pageIdentifier.equals("personalDetails") && !pageIdentifier.equals("contact") &&
                !pageIdentifier.equals("education") && !pageIdentifier.equals("professional") &&
                !pageIdentifier.equals("additional")) {
            throw new BadRequestException("Invalid page identifier: " + pageIdentifier);
        }

        int employeeId = employee.getEmployeeId();

        String onboardingDetailsJson = (String) redisTemplate.opsForValue().get("onboarding:" + email);
        OnboardingDetails onboardingDetailsFromRedis = null;
        try {
            onboardingDetailsFromRedis = new ObjectMapper().readValue(onboardingDetailsJson,
                    OnboardingDetails.class);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("onboarddddddddddddddddddddding obj: " + onboardingDetailsFromRedis);

        if (pageIdentifier.equals("personalDetails")) {

            fillInPersonalDetails(onboardingDetails, onboardingDetailsFromRedis, employee, employeeId);

        } else if (pageIdentifier.equals("contact")) {

            fillInContactDetails(onboardingDetails, onboardingDetailsFromRedis, employee, employeeId);

        } else if (pageIdentifier.equals("education")) {

            fillInEducationDetails(onboardingDetails, onboardingDetailsFromRedis, employee, employeeId);

        } else if (pageIdentifier.equals("professional")) {

            fillInProfessionalDetails(onboardingDetails, onboardingDetailsFromRedis, employee, employeeId);

        } else if (pageIdentifier.equals("additional")) {

            fillInOtherDetails(onboardingDetails, onboardingDetailsFromRedis, employee, employeeId);

        }

        System.out.println("after filling detailssssssssssss: " + onboardingDetailsFromRedis);

        try {
            onboardingDetailsJson = new ObjectMapper().writeValueAsString(onboardingDetailsFromRedis);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        redisTemplate.opsForValue().set("onboarding:" + email, onboardingDetailsJson);

        System.out.println("json redisssssssssssssssssss: " + onboardingDetailsJson);

        return new OnboardingResponseDTO(onboardingDetailsFromRedis, employeeId, employee.getStatus(), pageIdentifier);

    }

    private void fillInPersonalDetails(OnboardingDetails onboardingDetails,
            OnboardingDetails onboardingDetailsFromRedis, Employee employee, int employeeId) {

        if (onboardingDetails.getPersonalDetails() == null ||
                onboardingDetails.getAadharCardDetails() == null ||
                onboardingDetails.getPanCardDetails() == null ||
                onboardingDetails.getPassportDetails() == null) {
            throw new FieldsMissingException(
                    "Please add all the mandatory fields(Personal & Identification details) in the Personal Details form.");
        }

        PersonalDetailsDTO personalDetailsDTO = onboardingDetails.getPersonalDetails();
        if (!personalDetailsDTO.isNull()) {
            PersonalDetails personalDetailsFromDB = personalDetailsRepository.findByEmployeeEmployeeId(employeeId);

            personalDetailsFromDB.setImageUrl(personalDetailsDTO.getImageUrl());
            personalDetailsFromDB.setGender(personalDetailsDTO.getGender());
            personalDetailsFromDB.setMotherName(personalDetailsDTO.getMotherName());
            personalDetailsFromDB.setFatherName(personalDetailsDTO.getFatherName());
            personalDetailsFromDB.setSecondaryMobile(personalDetailsDTO.getSecondaryMobile());
            personalDetailsRepository.save(personalDetailsFromDB);

            String imageUrl = personalDetailsDTO.getImageUrl();
            if (imageUrl != null) {
                imageUrl = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                String namePart = imageUrl.split("__")[0];
                String extension = imageUrl.substring(imageUrl.lastIndexOf('.'));
                personalDetailsDTO.setImageUrl(namePart + extension);

            }
            onboardingDetailsFromRedis.setPersonalDetails(personalDetailsDTO);

            String firstName = employee.getFirstName() == null ? "" : employee.getFirstName() + " ";
            String middleName = employee.getMiddleName() == null ? "" : employee.getMiddleName() + " ";
            String lastName = employee.getLastName() == null ? "" : employee.getLastName();
            String fullName = firstName + middleName + lastName;
            fullName = fullName.trim();

            if (!personalDetailsDTO.getFullName().equals(fullName)) {
                String[] parts = personalDetailsDTO.getFullName().split(" ");
                if (parts.length == 1) {
                    employee.setFirstName(parts[0]);
                } else if (parts.length == 2) {
                    employee.setFirstName(parts[0]);
                    employee.setLastName(parts[1]);
                } else {
                    employee.setFirstName(parts[0]);
                    employee.setLastName(parts[parts.length - 1]);
                    employee.setMiddleName(String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1)));
                }
                employeeRepository.save(employee);
            }
        }

        AadharCardDetailsDTO aadharCardDetailsDTO = onboardingDetails.getAadharCardDetails();
        if (!aadharCardDetailsDTO.isNull()) {
            AadharCardDetailsDTO aadharCardDetailsFromRedis = onboardingDetailsFromRedis.getAadharCardDetails();
            if (aadharCardDetailsFromRedis != null) {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                IdentificationDetails identificationDetailsFromDB = identificationDetailsRepository
                        .findByEmployeeAndType(employeeId, "AADHAR");
                identificationDetailsFromDB
                        .setIdentificationNumber(aadharCardDetailsDTO.getAadharIdentificationNumber());
                identificationDetailsFromDB.setIdentificationUrl(aadharCardDetailsDTO.getAadharIdentificationUrl());
                identificationDetailsRepository.save(identificationDetailsFromDB);
            } else {
                IdentificationDetails identificationDetails = new IdentificationDetails();
                identificationDetails.setIdentificationNumber(aadharCardDetailsDTO.getAadharIdentificationNumber());
                identificationDetails.setIdentificationUrl(aadharCardDetailsDTO.getAadharIdentificationUrl());
                identificationDetails.setIdentityType(IdentityType.AADHAR);
                identificationDetails.setEmployee(employee);
                identificationDetailsRepository.save(identificationDetails);
            }

            String identificationUrl = aadharCardDetailsDTO.getAadharIdentificationUrl();
            if (identificationUrl != null) {

                identificationUrl = identificationUrl.substring(identificationUrl.lastIndexOf('/') + 1);
                String namePart = identificationUrl.split("__")[0];
                String extension = identificationUrl.substring(identificationUrl.lastIndexOf('.'));
                aadharCardDetailsDTO.setAadharIdentificationUrl(namePart + extension);
            }
            onboardingDetailsFromRedis.setAadharCardDetails(aadharCardDetailsDTO);

        }

        PanCardDetailsDTO panCardDetailsDTO = onboardingDetails.getPanCardDetails();
        if (!panCardDetailsDTO.isNull()) {
            PanCardDetailsDTO panCardDetailsFromRedis = onboardingDetailsFromRedis.getPanCardDetails();
            if (panCardDetailsFromRedis != null) {
                IdentificationDetails identificationDetailsFromDB = identificationDetailsRepository
                        .findByEmployeeAndType(employeeId, "PAN");
                identificationDetailsFromDB.setIdentificationNumber(panCardDetailsDTO.getPanIdentificationNumber());
                identificationDetailsFromDB.setIdentificationUrl(panCardDetailsDTO.getPanIdentificationUrl());
                identificationDetailsRepository.save(identificationDetailsFromDB);
            } else {
                IdentificationDetails identificationDetails = new IdentificationDetails();
                identificationDetails.setIdentificationNumber(panCardDetailsDTO.getPanIdentificationNumber());
                identificationDetails.setIdentificationUrl(panCardDetailsDTO.getPanIdentificationUrl());
                identificationDetails.setIdentityType(IdentityType.PAN);
                identificationDetails.setEmployee(employee);
                identificationDetailsRepository.save(identificationDetails);
            }

            String identificationUrl = panCardDetailsDTO.getPanIdentificationUrl();
            if (identificationUrl != null) {
                identificationUrl = identificationUrl.substring(identificationUrl.lastIndexOf('/') + 1);
                String namePart = identificationUrl.split("__")[0];
                String extension = identificationUrl.substring(identificationUrl.lastIndexOf('.'));
                panCardDetailsDTO.setPanIdentificationUrl(namePart + extension);

            }
            onboardingDetailsFromRedis.setPanCardDetails(panCardDetailsDTO);
        }

        PassportDetailsDTO passportDetailsDTO = onboardingDetails.getPassportDetails();
        if (!passportDetailsDTO.isNull()) {
            PassportDetailsDTO passportDetailsFromRedis = onboardingDetailsFromRedis.getPassportDetails();
            if (passportDetailsFromRedis != null) {
                PassportDetails passportDetailsFromDB = passportDetailsRepository.findByEmployeeEmployeeId(employeeId);
                passportDetailsFromDB.setPassportNumber(passportDetailsDTO.getPassportNumber());
                passportDetailsFromDB.setPassportUrl(passportDetailsDTO.getPassportUrl());
                passportDetailsRepository.save(passportDetailsFromDB);
            } else {
                PassportDetails passportDetails = dtoToEntity(passportDetailsDTO, PassportDetails.class);
                passportDetails.setEmployee(employee);
                passportDetailsRepository.save(passportDetails);
            }

            String identificationUrl = passportDetailsDTO.getPassportUrl();
            if (identificationUrl != null) {
                identificationUrl = identificationUrl.substring(identificationUrl.lastIndexOf('/') + 1);
                String namePart = identificationUrl.split("__")[0];
                String extension = identificationUrl.substring(identificationUrl.lastIndexOf('.'));
                passportDetailsDTO.setPassportUrl(namePart + extension);

            }
            onboardingDetailsFromRedis.setPassportDetails(passportDetailsDTO);
        }

    }

    private void fillInContactDetails(OnboardingDetails onboardingDetails,
            OnboardingDetails onboardingDetailsFromRedis, Employee employee, int employeeId) {

        if (onboardingDetails.getAddress() == null) {
            throw new FieldsMissingException(
                    "Please add all the mandatory fields(address details) in the Contact Details form.");
        }

        List<AddressDTO> addressDTOs = onboardingDetails.getAddress();
        if (addressDTOs.isEmpty()) {
            throw new FieldsMissingException(
                    "Please fill all the mandatory fields(Address details) in the Contact details form.");
        }

        List<AddressDTO> addressDetailsFromRedis = onboardingDetailsFromRedis.getAddress();
        if (addressDetailsFromRedis != null) {
            addressRepository.deleteAllByEmployeeId(employeeId);
        }
        addressDTOs.forEach(addressDTO -> {
            Address address = dtoToEntity(addressDTO, Address.class);
            address.setEmployee(employee);
            addressRepository.save(address);
        });

        onboardingDetailsFromRedis.setAddress(addressDTOs);

    }

    private void fillInEducationDetails(OnboardingDetails onboardingDetails,
            OnboardingDetails onboardingDetailsFromRedis, Employee employee, int employeeId) {

        List<EducationDTO> educationDTOs = onboardingDetails.getEducation();
        List<EmploymentHistoryDTO> employmentHistoryDTOs = onboardingDetails.getEmploymentHistories();

        if (educationDTOs == null || employmentHistoryDTOs == null) {
            throw new FieldsMissingException(
                    "Please add all the mandatory fields(education & employment histories details) in the Education Details form.");
        }

        if (educationDTOs.isEmpty()) {
            throw new FieldsMissingException(
                    "Please fill all the mandatory fields(Education & Employment history details) in the Education details form.");
        }

        if (!educationDTOs.isEmpty()) {

            List<EducationDTO> educationFromRedis = onboardingDetailsFromRedis.getEducation();
            if (educationFromRedis != null) {
                educationRepository.deleteAllByEmployeeId(employeeId);
            }
            educationDTOs.forEach(educationDTO -> {
                Education educationDetails = dtoToEntity(educationDTO, Education.class);
                educationDetails.setEmployee(employee);
                educationRepository.save(educationDetails);
            });

            educationDTOs.forEach(educationDTO -> {
                String degreeCertificateUrl = educationDTO.getDegreeCertificateUrl();
                if (degreeCertificateUrl != null) {
                    degreeCertificateUrl = degreeCertificateUrl.substring(degreeCertificateUrl.lastIndexOf('/') + 1);
                    String namePart = degreeCertificateUrl.split("__")[0];
                    String extension = degreeCertificateUrl.substring(degreeCertificateUrl.lastIndexOf('.'));
                    educationDTO.setDegreeCertificateUrl(namePart + extension);

                }
            });
            onboardingDetailsFromRedis.setEducation(educationDTOs);
        }

        if (!employmentHistoryDTOs.isEmpty()) {
            List<EmploymentHistoryDTO> employmentHistoryFromRedis = onboardingDetailsFromRedis
                    .getEmploymentHistories();
            if (employmentHistoryFromRedis != null) {
                employmentHistoryRepository.deleteAllByEmployeeId(employeeId);
            }
            employmentHistoryDTOs.forEach(employmentHistoryDTO -> {
                EmploymentHistory employmentHistory = dtoToEntity(employmentHistoryDTO, EmploymentHistory.class);
                employmentHistory.setEmployee(employee);
                employmentHistoryRepository.save(employmentHistory);
            });

            employmentHistoryDTOs.forEach(employmentHistoryDTO -> {
                String experienceCertificateUrl = employmentHistoryDTO.getExperienceCertificateUrl();
                if (experienceCertificateUrl != null) {
                    experienceCertificateUrl = experienceCertificateUrl
                            .substring(experienceCertificateUrl.lastIndexOf('/') + 1);
                    employmentHistoryDTO.setExperienceCertificateUrl(experienceCertificateUrl);

                }

                // Process relievingLetterUrl
                String relievingLetterUrl = employmentHistoryDTO.getRelievingLetterUrl();
                if (relievingLetterUrl != null && relievingLetterUrl.contains("/")) {
                    relievingLetterUrl = relievingLetterUrl.substring(relievingLetterUrl.lastIndexOf('/') + 1);
                    String namePart = relievingLetterUrl.split("__")[0];
                    String extension = relievingLetterUrl.substring(relievingLetterUrl.lastIndexOf('.'));
                    employmentHistoryDTO.setRelievingLetterUrl(namePart + extension);
                }

                // Process appointmentLetterUrl
                String appointmentLetterUrl = employmentHistoryDTO.getAppointmentLetterUrl();
                if (appointmentLetterUrl != null && appointmentLetterUrl.contains("/")) {
                    appointmentLetterUrl = appointmentLetterUrl.substring(appointmentLetterUrl.lastIndexOf('/') + 1);
                    String namePart = appointmentLetterUrl.split("__")[0];
                    String extension = appointmentLetterUrl.substring(appointmentLetterUrl.lastIndexOf('.'));
                    employmentHistoryDTO.setAppointmentLetterUrl(namePart + extension);
                }

                // Process lastMonthSalarySlip1Url
                String lastMonthSalarySlip1Url = employmentHistoryDTO.getLastMonthSalarySlip1Url();
                if (lastMonthSalarySlip1Url != null && lastMonthSalarySlip1Url.contains("/")) {
                    lastMonthSalarySlip1Url = lastMonthSalarySlip1Url
                            .substring(lastMonthSalarySlip1Url.lastIndexOf('/') + 1);
                    String namePart = lastMonthSalarySlip1Url.split("__")[0];
                    String extension = lastMonthSalarySlip1Url.substring(lastMonthSalarySlip1Url.lastIndexOf('.'));
                    employmentHistoryDTO.setLastMonthSalarySlip1Url(namePart + extension);
                }

                // Process lastMonthSalarySlip2Url
                String lastMonthSalarySlip2Url = employmentHistoryDTO.getLastMonthSalarySlip2Url();
                if (lastMonthSalarySlip2Url != null && lastMonthSalarySlip2Url.contains("/")) {
                    lastMonthSalarySlip2Url = lastMonthSalarySlip2Url
                            .substring(lastMonthSalarySlip2Url.lastIndexOf('/') + 1);
                    String namePart = lastMonthSalarySlip2Url.split("__")[0];
                    String extension = lastMonthSalarySlip2Url.substring(lastMonthSalarySlip2Url.lastIndexOf('.'));
                    employmentHistoryDTO.setLastMonthSalarySlip2Url(namePart + extension);
                }

                // Process lastMonthSalarySlip3Url
                String lastMonthSalarySlip3Url = employmentHistoryDTO.getLastMonthSalarySlip3Url();
                if (lastMonthSalarySlip3Url != null && lastMonthSalarySlip3Url.contains("/")) {
                    lastMonthSalarySlip3Url = lastMonthSalarySlip3Url
                            .substring(lastMonthSalarySlip3Url.lastIndexOf('/') + 1);
                    String namePart = lastMonthSalarySlip3Url.split("__")[0];
                    String extension = lastMonthSalarySlip3Url.substring(lastMonthSalarySlip3Url.lastIndexOf('.'));
                    employmentHistoryDTO.setLastMonthSalarySlip3Url(namePart + extension);
                }
            });
            onboardingDetailsFromRedis.setEmploymentHistories(employmentHistoryDTOs);
        }

    }

    private void fillInProfessionalDetails(OnboardingDetails onboardingDetails,
            OnboardingDetails onboardingDetailsFromRedis, Employee employee, int employeeId) {

        List<ProfessionalReferencesDTO> professionalReferencesDTOs = onboardingDetails.getProfessionalReferences();
        List<RelativesDTO> relativesDTOs = onboardingDetails.getRelatives();
        PassportDetailsDTO passportDetailsDTO = onboardingDetails.getPassportDetails();
        VisaDetailsDTO visaDetailsDTO = onboardingDetails.getVisaDetails();

        if (professionalReferencesDTOs == null || relativesDTOs == null
                || passportDetailsDTO == null || visaDetailsDTO == null) {
            throw new FieldsMissingException(
                    "Please add all the mandatory fields(Professional references, relatives, passport & visa details) in the Professional Details form.");
        }

        if (!professionalReferencesDTOs.isEmpty()) {
            List<ProfessionalReferencesDTO> professionalReferencesFromRedis = onboardingDetailsFromRedis
                    .getProfessionalReferences();
            if (professionalReferencesFromRedis != null) {
                professionalReferencesRepository.deleteAllByEmployeeId(employeeId);
            }
            professionalReferencesDTOs.forEach(professionalReferencesDTO -> {
                ProfessionalReferences professionalReference = dtoToEntity(professionalReferencesDTO,
                        ProfessionalReferences.class);
                professionalReference.setEmployee(employee);
                professionalReferencesRepository.save(professionalReference);
            });

            onboardingDetailsFromRedis.setProfessionalReferences(professionalReferencesDTOs);
        }

        if (!relativesDTOs.isEmpty()) {
            List<RelativesDTO> relativesFromRedis = onboardingDetailsFromRedis.getRelatives();
            if (relativesFromRedis != null) {
                relativesRepository.deleteAllByEmployeeId(employeeId);
            }
            onboardingDetailsFromRedis.setRelatives(relativesDTOs);
            relativesDTOs.forEach(relativesDTO -> {
                Relatives relative = dtoToEntity(relativesDTO, Relatives.class);
                relative.setEmployee(employee);
                relativesRepository.save(relative);
            });
        }

        if (!passportDetailsDTO.isNull()) {
            PassportDetailsDTO passportDetailsFromRedis = onboardingDetailsFromRedis.getPassportDetails();
            if (passportDetailsFromRedis != null) {
                PassportDetails passportDetailsFromDB = passportDetailsRepository
                        .findByEmployeeEmployeeId(employeeId);
                passportDetailsFromDB.setPassportNumber(passportDetailsDTO.getPassportNumber());
                passportDetailsFromDB.setDateOfIssue(passportDetailsDTO.getDateOfIssue());
                passportDetailsFromDB.setNationality(passportDetailsDTO.getNationality());
                passportDetailsFromDB.setPlaceOfIssue(passportDetailsDTO.getPlaceOfIssue());
                passportDetailsFromDB.setCountryOfIssue(passportDetailsDTO.getCountryOfIssue());
                passportDetailsFromDB.setValidUpto(passportDetailsDTO.getValidUpto());
                passportDetailsRepository.save(passportDetailsFromDB);
            } else {
                PassportDetails passportDetails = dtoToEntity(passportDetailsDTO, PassportDetails.class);
                passportDetails.setEmployee(employee);
                passportDetailsRepository.save(passportDetails);
            }

            String identificationUrl = passportDetailsDTO.getPassportUrl();
            if (identificationUrl != null) {
                identificationUrl = identificationUrl.substring(identificationUrl.lastIndexOf('/') + 1);
                String namePart = identificationUrl.split("__")[0];
                String extension = identificationUrl.substring(identificationUrl.lastIndexOf('.'));
                passportDetailsDTO.setPassportUrl(namePart + extension);

            }
            onboardingDetailsFromRedis.setPassportDetails(passportDetailsDTO);

        }

        if (!visaDetailsDTO.isNull()) {
            VisaDetailsDTO visaDetailsFromRedis = onboardingDetailsFromRedis.getVisaDetails();
            if (visaDetailsFromRedis != null) {
                VisaDetails visaDetailsFromDB = visaDetailsRepository.findByEmployeeEmployeeId(employeeId);
                visaDetailsFromDB.setCountry(visaDetailsDTO.getCountry());
                visaDetailsFromDB.setPassportCopy(visaDetailsDTO.getPassportCopy());
                visaDetailsFromDB.setPassportCopyUrl(visaDetailsDTO.getPassportCopyUrl());
                visaDetailsFromDB.setStatus(visaDetailsDTO.getStatus());
                visaDetailsFromDB.setWorkPermitDetails(visaDetailsDTO.getWorkPermitDetails());
                visaDetailsFromDB.setWorkPermitValidTill(visaDetailsDTO.getWorkPermitValidTill());
                visaDetailsRepository.save(visaDetailsFromDB);
            } else {
                VisaDetails visaDetails = dtoToEntity(visaDetailsDTO, VisaDetails.class);
                visaDetails.setEmployee(employee);
                visaDetailsRepository.save(visaDetails);
            }

            String identificationUrl = visaDetailsDTO.getPassportCopyUrl();
            if (identificationUrl != null) {
                identificationUrl = identificationUrl.substring(identificationUrl.lastIndexOf('/') + 1);
                String namePart = identificationUrl.split("__")[0];
                String extension = identificationUrl.substring(identificationUrl.lastIndexOf('.'));
                visaDetailsDTO.setPassportCopyUrl(namePart+extension);

            }
            onboardingDetailsFromRedis.setVisaDetails(visaDetailsDTO);
        }

    }

    private void fillInOtherDetails(OnboardingDetails onboardingDetails,
            OnboardingDetails onboardingDetailsFromRedis, Employee employee, int employeeId) {
        AdditionalDetailsDTO additionalDetailsDTO = onboardingDetails.getAdditionalDetails();

        if (additionalDetailsDTO == null) {
            throw new FieldsMissingException(
                    "Please add all the mandatory fields(Other details) in the Other details form.");
        }

        if (!additionalDetailsDTO.isNull()) {
            AdditionalDetailsDTO otherDetailsFromRedis = onboardingDetailsFromRedis.getAdditionalDetails();
            if (otherDetailsFromRedis != null) {
                AdditionalDetails otherDetailsFromDB = otherDetailsRepository.findByEmployeeEmployeeId(employeeId);
                otherDetailsFromDB.setHobbiesDeclaration(additionalDetailsDTO.getHobbiesDeclaration());
                otherDetailsFromDB.setIllnessDeclaration(additionalDetailsDTO.getIllnessDeclaration());
                otherDetailsRepository.save(otherDetailsFromDB);
            } else {
                AdditionalDetails otherDetails = dtoToEntity(additionalDetailsDTO, AdditionalDetails.class);
                otherDetails.setEmployee(employee);
                otherDetailsRepository.save(otherDetails);
            }
            onboardingDetailsFromRedis.setAdditionalDetails(additionalDetailsDTO);
        }

    }

    private <D, E> E dtoToEntity(D dto, Class<E> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    @Override
    public OnboardingResponseDTO getOnboardingDetails(String email, String pageIdentifier) {

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);
        if (!pageIdentifier.equals("personalDetails") && !pageIdentifier.equals("contact") &&
                !pageIdentifier.equals("education") && !pageIdentifier.equals("professional") &&
                !pageIdentifier.equals("additional")) {
            throw new NotFoundException("Invalid page identifier: " + pageIdentifier);
        }

        String onboardingDetailsJson = (String) redisTemplate.opsForValue().get("onboarding:" + email);
        OnboardingDetails onboardingDetailsFromRedis = null;
        try {
            onboardingDetailsFromRedis = new ObjectMapper().readValue(onboardingDetailsJson,
                    OnboardingDetails.class);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // fetchOnboardingDetails(onboardingDetails, employeeId, pageIdentifier);

        return new OnboardingResponseDTO(onboardingDetailsFromRedis, employee.getEmployeeId(), employee.getStatus(),
                pageIdentifier);
    }

    @Override
    public PreviewResponseDTO getAllOnboardingDetails(String email) {

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);

        String onboardingDetailsJson = (String) redisTemplate.opsForValue().get("onboarding:" + email);
        OnboardingDetails onboardingDetailsFromRedis = null;
        try {
            onboardingDetailsFromRedis = new ObjectMapper().readValue(onboardingDetailsJson,
                    OnboardingDetails.class);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String firstName = employee.getFirstName() == null ? "" : employee.getFirstName() + " ";
        String middleName = employee.getMiddleName() == null ? "" : employee.getMiddleName() + " ";
        String lastName = employee.getLastName() == null ? "" : employee.getLastName();
        String fullName = firstName + middleName + lastName;
        fullName = fullName.trim();

        EmployeeDTO employeeDTO = new EmployeeDTO(employee.getEmployeeId(), fullName, employee.getMobileNumber(),
                employee.getDateOfBirth(),
                employee.getEmail());

        return new PreviewResponseDTO(onboardingDetailsFromRedis, employeeDTO, employee.getStatus());
    }

    @Override
    public String updateEmployeeStatus(String email, String status) {

        Employee employee = employeeRepository.findByEmail(email);
        employee.setStatus(status);

        return "For employee " + email + ", status is updated from PENDING to " + status + " succeesully.";
    }

    @Override
    public GeneralResponse notifyAdmin(String email) {

        emailService.sendEmail(adminEmail, null,
                EmailConstant.VERIFY_EMPLOYEE_DETAILS_SUBJECT, EmailConstant.VERIFY_EMPLOYEE_DETAILS_TEMPLATE, email);

        return new GeneralResponse("An email is successfully sent to Admin to verify employee " + email + " details.");
    }

    @Override
    public void addPreviewDetails(String email, PreviewDto previewDto) {
        Employee employee = employeeRepository.findByEmail(email);

        PreviewDetails previewDetails = new PreviewDetails();
        previewDetails.setEmployee(employee);
        previewDetails.setSignatureUrl(previewDto.getSignatureUrl());
        previewDetails.setDate(previewDto.getDate());
        previewDetails.setPlace(previewDto.getPlace());

        previewRepository.save(previewDetails);
    }

    public void createOnboardingObj(String email) {
        OnboardingDetails onboardingDetails = new OnboardingDetails();
        String onboardingDetailsJson = null;
        try {
            onboardingDetailsJson = new ObjectMapper().writeValueAsString(onboardingDetails);
            System.out.println("iiiiiiiiiiiiiiiiii"+onboardingDetailsJson);
        } catch (JsonProcessingException e) {
            System.out.println("errrrrrrrrrrror"+e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        redisTemplate.opsForValue().set("onboarding:" + email, onboardingDetailsJson, ONBOARDING_OBJ_EXPIRATION_TIME,
                TimeUnit.DAYS);
    }

}
