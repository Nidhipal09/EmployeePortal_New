package com.employeeportal.serviceImpl.onboarding;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.employeeportal.config.EmailConstant;
import com.employeeportal.dto.onboarding.AadharCardDetailsDTO;
import com.employeeportal.dto.onboarding.AdditionalDetailsDTO;
import com.employeeportal.dto.onboarding.AddressDTO;
import com.employeeportal.dto.onboarding.EducationDTO;
import com.employeeportal.dto.onboarding.EmployeeDTO;
import com.employeeportal.dto.onboarding.OnboardingResponseDTO;
import com.employeeportal.dto.onboarding.PanCardDetailsDTO;
import com.employeeportal.dto.onboarding.PassportDetailsDTO;
import com.employeeportal.dto.onboarding.PersonalDetailsDTO;
import com.employeeportal.dto.onboarding.PreviewResponseDTO;
import com.employeeportal.dto.onboarding.EmploymentHistoryDTO;
import com.employeeportal.dto.onboarding.GeneralResponse;
import com.employeeportal.dto.onboarding.ProfessionalReferencesDTO;
import com.employeeportal.dto.onboarding.RelativesDTO;
import com.employeeportal.dto.onboarding.VisaDetailsDTO;
import com.employeeportal.exception.FieldsMissingException;
import com.employeeportal.exception.NotFoundException;
import com.employeeportal.model.onboarding.AdditionalDetails;
import com.employeeportal.model.onboarding.Address;
import com.employeeportal.model.onboarding.Education;
import com.employeeportal.model.onboarding.IdentificationDetails;
import com.employeeportal.model.onboarding.IdentityType;
import com.employeeportal.model.onboarding.OnboardingDetails;
import com.employeeportal.model.onboarding.PassportDetails;
import com.employeeportal.model.onboarding.PersonalDetails;
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
import com.employeeportal.repository.onboarding.ProfessionalReferencesRepository;
import com.employeeportal.repository.onboarding.RelativesRepository;
import com.employeeportal.repository.onboarding.VisaDetailsRepository;
import com.employeeportal.repository.registration.EmployeeRepository;
import com.employeeportal.service.EmailService;
import com.employeeportal.service.onboarding.OnboardingService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private EmailService emailService;

    @Override
    public OnboardingResponseDTO fillOnboardingDetails(OnboardingDetails onboardingDetails, String email,
            String pageIdentifier) {

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);
        if (!pageIdentifier.equals("personalDetails") && !pageIdentifier.equals("contact") &&
                !pageIdentifier.equals("education") && !pageIdentifier.equals("professional") &&
                !pageIdentifier.equals("additional")) {
            throw new NotFoundException("Invalid page identifier: " + pageIdentifier);
        }

        int employeeId = employee.getEmployeeId();

        if (pageIdentifier.equals("personalDetails")) {

            // if (onboardingDetails.getPersonalDetails().isNull() ||
            // onboardingDetails.getAadharCardDetails().isNull() ||
            // onboardingDetails.getPanCardDetails().isNull()) {
            // throw new FieldsMissingException(
            // "Please fill all the mandatory fields(Personal & Identification details) in
            // the Personal Details form.");
            // }

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

                personalDetailsRepository.save(personalDetailsFromDB);
            }

            AadharCardDetailsDTO aadharCardDetailsDTO = onboardingDetails.getAadharCardDetails();
            if (!aadharCardDetailsDTO.isNull()) {
                IdentificationDetails identificationDetailsFromDB = identificationDetailsRepository
                        .findByEmployeeAndType(employeeId, "AADHAR");
                if (identificationDetailsFromDB != null) {
                    identificationDetailsRepository.deleteByEmployeeIdAndType(employeeId, "AADHAR");
                }
                IdentificationDetails identificationDetails = new IdentificationDetails();
                identificationDetails.setIdentificationNumber(aadharCardDetailsDTO.getAadharIdentificationNumber());
                identificationDetails.setIdentificationUrl(aadharCardDetailsDTO.getAadharIdentificationUrl());
                identificationDetails.setIdentityType(IdentityType.AADHAR);
                identificationDetails.setEmployee(employee);
                identificationDetailsRepository.save(identificationDetails);

            }

            PanCardDetailsDTO panCardDetailsDTO = onboardingDetails.getPanCardDetails();
            if (!panCardDetailsDTO.isNull()) {
                IdentificationDetails identificationDetailsFromDB = identificationDetailsRepository
                        .findByEmployeeAndType(employeeId, "PAN");
                if (identificationDetailsFromDB != null) {
                    identificationDetailsRepository.deleteByEmployeeIdAndType(employeeId, "PAN");
                }
                IdentificationDetails identificationDetails = new IdentificationDetails();
                identificationDetails.setIdentificationNumber(panCardDetailsDTO.getPanIdentificationNumber());
                identificationDetails.setIdentificationUrl(panCardDetailsDTO.getPanIdentificationUrl());
                identificationDetails.setIdentityType(IdentityType.PAN);
                identificationDetails.setEmployee(employee);
                identificationDetailsRepository.save(identificationDetails);

            }

            PassportDetailsDTO passportDetailsDTO = onboardingDetails.getPassportDetails();
            if (!passportDetailsDTO.isNull()) {
                PassportDetails passportDetailsFromDB = passportDetailsRepository.findByEmployeeEmployeeId(employeeId);
                if (passportDetailsFromDB != null) {
                    passportDetailsFromDB.setPassportNumber(passportDetailsDTO.getPassportNumber());
                    passportDetailsFromDB.setPassportUrl(passportDetailsDTO.getPassportUrl());
                    passportDetailsRepository.save(passportDetailsFromDB);
                } else {
                    PassportDetails passportDetails = dtoToEntity(passportDetailsDTO, PassportDetails.class);
                    passportDetails.setEmployee(employee);
                    passportDetailsRepository.save(passportDetails);
                }

            }

            fetchOnboardingDetails(onboardingDetails, employeeId, "contact");
            fetchOnboardingDetails(onboardingDetails, employeeId, "education");
            fetchOnboardingDetails(onboardingDetails, employeeId, "professional");
            fetchOnboardingDetails(onboardingDetails, employeeId, "additional");

        } else if (pageIdentifier.equals("contact")) {

            if (onboardingDetails.getAddress() == null) {
                throw new FieldsMissingException(
                        "Please add all the mandatory fields(address details) in the Contact Details form.");
            }

            List<AddressDTO> addressDTOs = onboardingDetails.getAddress();
            if (addressDTOs.isEmpty()) {
                throw new FieldsMissingException(
                        "Please fill all the mandatory fields(Address details) in the Contact details form.");
            }

            if (!addressDTOs.isEmpty()) {
                List<Address> addressesFromDB = addressRepository.findByEmployeeEmployeeId(employeeId);
                if (!addressesFromDB.isEmpty()) {
                    addressRepository.deleteAllByEmployeeId(employeeId);
                }
                addressDTOs.forEach(addressDTO -> {
                    Address address = dtoToEntity(addressDTO, Address.class);
                    address.setEmployee(employee);
                    addressRepository.save(address);
                });
            }

            fetchOnboardingDetails(onboardingDetails, employeeId, "personalDetails");
            fetchOnboardingDetails(onboardingDetails, employeeId, "education");
            fetchOnboardingDetails(onboardingDetails, employeeId, "professional");
            fetchOnboardingDetails(onboardingDetails, employeeId, "additional");

        } else if (pageIdentifier.equals("education")) {

            if (onboardingDetails.getEducation() == null || onboardingDetails.getEmploymentHistories() == null) {
                throw new FieldsMissingException(
                        "Please add all the mandatory fields(education & employment histories details) in the Education Details form.");
            }

            if (onboardingDetails.getEducation().isEmpty()) {
                throw new FieldsMissingException(
                        "Please fill all the mandatory fields(Education & Employment history details) in the Education details form.");
            }

            List<EducationDTO> educationDTOs = onboardingDetails.getEducation();
            if (!educationDTOs.isEmpty()) {
                List<Education> educationFromDB = educationRepository.findByEmployeeEmployeeId(employeeId);
                if (!educationFromDB.isEmpty()) {
                    educationRepository.deleteAllByEmployeeId(employeeId);
                }
                educationDTOs.forEach(educationDTO -> {
                    Education educationDetails = dtoToEntity(educationDTO, Education.class);
                    educationDetails.setEmployee(employee);
                    educationRepository.save(educationDetails);
                });
            }

            List<EmploymentHistoryDTO> employmentHistoryDTOs = onboardingDetails.getEmploymentHistories();
            if (!employmentHistoryDTOs.isEmpty()) {
                List<EmploymentHistory> employmentHistoryFromDB = employmentHistoryRepository
                        .findByEmployeeEmployeeId(employeeId);
                if (!employmentHistoryFromDB.isEmpty()) {
                    employmentHistoryRepository.deleteAllByEmployeeId(employeeId);
                }
                employmentHistoryDTOs.forEach(employmentHistoryDTO -> {
                    EmploymentHistory employmentHistory = dtoToEntity(employmentHistoryDTO, EmploymentHistory.class);
                    employmentHistory.setEmployee(employee);
                    employmentHistoryRepository.save(employmentHistory);
                });
            }

            fetchOnboardingDetails(onboardingDetails, employeeId, "personalDetails");
            fetchOnboardingDetails(onboardingDetails, employeeId, "contact");
            fetchOnboardingDetails(onboardingDetails, employeeId, "professional");
            fetchOnboardingDetails(onboardingDetails, employeeId, "additional");

        } else if (pageIdentifier.equals("professional")) {

            if (onboardingDetails.getProfessionalReferences() == null || onboardingDetails.getRelatives() == null
                    || onboardingDetails.getPassportDetails() == null || onboardingDetails.getVisaDetails() == null) {
                throw new FieldsMissingException(
                        "Please add all the mandatory fields(Professional references, relatives, passport & visa details) in the Professional Details form.");
            }

            List<ProfessionalReferencesDTO> professionalReferencesDTOs = onboardingDetails.getProfessionalReferences();
            if (!professionalReferencesDTOs.isEmpty()) {
                List<ProfessionalReferences> professionalReferencesFromDB = professionalReferencesRepository
                        .findByEmployeeEmployeeId(employeeId);
                if (!professionalReferencesFromDB.isEmpty()) {
                    professionalReferencesRepository.deleteAllByEmployeeId(employeeId);
                }
                professionalReferencesDTOs.forEach(professionalReferencesDTO -> {
                    ProfessionalReferences professionalReference = dtoToEntity(professionalReferencesDTO,
                            ProfessionalReferences.class);
                    professionalReference.setEmployee(employee);
                    professionalReferencesRepository.save(professionalReference);
                });
            }

            List<RelativesDTO> relativesDTOs = onboardingDetails.getRelatives();
            if (!relativesDTOs.isEmpty()) {
                List<Relatives> relativesFromDB = relativesRepository.findByEmployeeEmployeeId(employeeId);
                if (!relativesFromDB.isEmpty()) {
                    relativesRepository.deleteAllByEmployeeId(employeeId);
                }
                relativesDTOs.forEach(relativesDTO -> {
                    Relatives relative = dtoToEntity(relativesDTO, Relatives.class);
                    relative.setEmployee(employee);
                    relativesRepository.save(relative);
                });
            }

            PassportDetailsDTO passportDetailsDTO = onboardingDetails.getPassportDetails();
            if (!passportDetailsDTO.isNull()) {
                PassportDetails passportDetailsFromDB = passportDetailsRepository.findByEmployeeEmployeeId(employeeId);
                if (passportDetailsFromDB != null) {
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
            }

            VisaDetailsDTO visaDetailsDTO = onboardingDetails.getVisaDetails();
            if (!visaDetailsDTO.isNull()) {
                VisaDetails visaDetailsDB = visaDetailsRepository.findByEmployeeEmployeeId(employeeId);
                if (visaDetailsDB != null) {
                    visaDetailsDB.setCountry(visaDetailsDTO.getCountry());
                    visaDetailsDB.setPassportCopy(visaDetailsDTO.getPassportCopy());
                    visaDetailsDB.setPassportCopyUrl(visaDetailsDTO.getPassportCopyUrl());
                    visaDetailsDB.setStatus(visaDetailsDTO.getStatus());
                    visaDetailsDB.setWorkPermitDetails(visaDetailsDTO.getWorkPermitDetails());
                    visaDetailsDB.setWorkPermitValidTill(visaDetailsDTO.getWorkPermitValidTill());
                    visaDetailsRepository.save(visaDetailsDB);
                } else {
                    VisaDetails visaDetails = dtoToEntity(visaDetailsDTO, VisaDetails.class);
                    visaDetails.setEmployee(employee);
                    visaDetailsRepository.save(visaDetails);
                }
            }

            fetchOnboardingDetails(onboardingDetails, employeeId, "personalDetails");
            fetchOnboardingDetails(onboardingDetails, employeeId, "education");
            fetchOnboardingDetails(onboardingDetails, employeeId, "contact");
            fetchOnboardingDetails(onboardingDetails, employeeId, "additional");

        } else if (pageIdentifier.equals("additional")) {

            if (onboardingDetails.getAdditionalDetails() == null) {
                throw new FieldsMissingException(
                        "Please add all the mandatory fields(Other details) in the Other details form.");
            }

            AdditionalDetailsDTO otherDetailsDTO = onboardingDetails.getAdditionalDetails();
            if (!otherDetailsDTO.isNull()) {
                AdditionalDetails otherDetailsFromDB = otherDetailsRepository.findByEmployeeEmployeeId(employeeId);
                if (otherDetailsFromDB != null) {
                    otherDetailsFromDB.setHobbiesDeclaration(otherDetailsDTO.getHobbiesDeclaration());
                    otherDetailsFromDB.setIllnessDeclaration(otherDetailsDTO.getIllnessDeclaration());
                    otherDetailsRepository.save(otherDetailsFromDB);
                } else {
                    AdditionalDetails otherDetails = dtoToEntity(otherDetailsDTO, AdditionalDetails.class);
                    otherDetails.setEmployee(employee);
                    otherDetailsRepository.save(otherDetails);
                }
            }

            fetchOnboardingDetails(onboardingDetails, employeeId, "personalDetails");
            fetchOnboardingDetails(onboardingDetails, employeeId, "education");
            fetchOnboardingDetails(onboardingDetails, employeeId, "contact");
            fetchOnboardingDetails(onboardingDetails, employeeId, "professional");

        }

        return new OnboardingResponseDTO(onboardingDetails, employee.getStatus(), pageIdentifier);

    }

    private <D, E> E dtoToEntity(D dto, Class<E> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    private <E, D> D entityToDto(E entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    private <E, D> List<D> entityListToDtoList(List<E> entityList, Class<D> dtoClass) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(entity -> entityToDto(entity, dtoClass))
                .collect(Collectors.toList());
    }

    @Override
    public OnboardingResponseDTO getOnboardingDetails(String email, String pageIdentifier) {
        OnboardingDetails onboardingDetails = new OnboardingDetails();

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);
        if (!pageIdentifier.equals("personalDetails") && !pageIdentifier.equals("contact") &&
                !pageIdentifier.equals("education") && !pageIdentifier.equals("professional") &&
                !pageIdentifier.equals("additional")) {
            throw new NotFoundException("Invalid page identifier: " + pageIdentifier);
        }

        int employeeId = employee.getEmployeeId();

        fetchOnboardingDetails(onboardingDetails, employeeId, pageIdentifier);

        return new OnboardingResponseDTO(onboardingDetails, employee.getStatus(), pageIdentifier);
    }

    private void fetchOnboardingDetails(OnboardingDetails onboardingDetails,
            int employeeId, String pageIdentifier) {
        if (pageIdentifier.equals("personalDetails")) {
            PersonalDetails personalDetails = personalDetailsRepository.findByEmployeeEmployeeId(employeeId);
            if (personalDetails != null) {
                PersonalDetailsDTO personalDetailsDTO = entityToDto(personalDetails, PersonalDetailsDTO.class);

                Employee employee = employeeRepository.findByEmployeeId(employeeId);
                String firstName = employee.getFirstName() == null ? "" : employee.getFirstName() + " ";
                String middleName = employee.getMiddleName() == null ? "" : employee.getMiddleName() + " ";
                String lastName = employee.getLastName() == null ? "" : employee.getLastName();
                String fullName = firstName + middleName + lastName;
                fullName = fullName.trim();
                personalDetailsDTO.setFullName(fullName);
                onboardingDetails.setPersonalDetails(personalDetailsDTO);
            }

            // Fetch and set identification details
            List<IdentificationDetails> identificationDetailsList = identificationDetailsRepository
                    .findByEmployeeEmployeeId(employeeId);
            if (!identificationDetailsList.isEmpty()) {
                identificationDetailsList.forEach(identificationDetails -> {
                    if (identificationDetails.getIdentityType() == IdentityType.AADHAR) {
                        AadharCardDetailsDTO aadharCardDetailsDTO = new AadharCardDetailsDTO();
                        aadharCardDetailsDTO
                                .setAadharIdentificationNumber(identificationDetails.getIdentificationNumber());
                        aadharCardDetailsDTO.setAadharIdentificationUrl(identificationDetails.getIdentificationUrl());
                        onboardingDetails.setAadharCardDetails(aadharCardDetailsDTO);
                    }
                    if (identificationDetails.getIdentityType() == IdentityType.PAN) {
                        PanCardDetailsDTO panCardDetailsDTO = new PanCardDetailsDTO();
                        panCardDetailsDTO.setPanIdentificationNumber(identificationDetails.getIdentificationNumber());
                        panCardDetailsDTO.setPanIdentificationUrl(identificationDetails.getIdentificationUrl());
                        onboardingDetails.setPanCardDetails(panCardDetailsDTO);
                    }
                });
            }

            PassportDetails passportDetails = passportDetailsRepository.findByEmployeeEmployeeId(employeeId);
            if (passportDetails != null) {
                PassportDetailsDTO passportDetailsDTO = entityToDto(passportDetails, PassportDetailsDTO.class);
                onboardingDetails.setPassportDetails(passportDetailsDTO);
            }
        } else if (pageIdentifier.equals("contact")) {

            // Fetch and set Address details
            List<Address> addressList = addressRepository.findByEmployeeEmployeeId(employeeId);
            if (!addressList.isEmpty())
                onboardingDetails.setAddress(entityListToDtoList(addressList, AddressDTO.class));

        } else if (pageIdentifier.equals("education")) {

            // Fetch and set Education details
            List<Education> educationList = educationRepository.findByEmployeeEmployeeId(employeeId);
            if (!educationList.isEmpty())
                onboardingDetails.setEducation(entityListToDtoList(educationList, EducationDTO.class));

            // Fetch and set Employment History details
            List<EmploymentHistory> employmentHistoryList = employmentHistoryRepository
                    .findByEmployeeEmployeeId(employeeId);
            if (!employmentHistoryList.isEmpty())
                onboardingDetails.setEmploymentHistories(
                        entityListToDtoList(employmentHistoryList, EmploymentHistoryDTO.class));

        } else if (pageIdentifier.equals("professional")) {
            // Fetch and set Professional References details
            List<ProfessionalReferences> professionalReferencesList = professionalReferencesRepository
                    .findByEmployeeEmployeeId(employeeId);
            if (!professionalReferencesList.isEmpty())
                onboardingDetails.setProfessionalReferences(
                        entityListToDtoList(professionalReferencesList, ProfessionalReferencesDTO.class));

            // Fetch and set Relatives details
            List<Relatives> relativesList = relativesRepository.findByEmployeeEmployeeId(employeeId);
            if (!relativesList.isEmpty())
                onboardingDetails.setRelatives(entityListToDtoList(relativesList, RelativesDTO.class));

            PassportDetails passportDetails = passportDetailsRepository.findByEmployeeEmployeeId(employeeId);
            if (passportDetails != null) {
                PassportDetailsDTO passportDetailsDTO = entityToDto(passportDetails, PassportDetailsDTO.class);
                onboardingDetails.setPassportDetails(passportDetailsDTO);
            }

            VisaDetails visaDetails = visaDetailsRepository.findByEmployeeEmployeeId(employeeId);
            if (visaDetails != null) {
                VisaDetailsDTO visaDetailsDTO = entityToDto(visaDetails, VisaDetailsDTO.class);
                onboardingDetails.setVisaDetails(visaDetailsDTO);
            }

        } else if (pageIdentifier.equals("additional")) {
            // Fetch and set Other details
            AdditionalDetails otherDetails = otherDetailsRepository.findByEmployeeEmployeeId(employeeId);
            if (otherDetails != null) {
                AdditionalDetailsDTO otherDetailsDTO = entityToDto(otherDetails, AdditionalDetailsDTO.class);
                onboardingDetails.setAdditionalDetails(otherDetailsDTO);
            }
        }

    }

    @Override
    public PreviewResponseDTO getAllOnboardingDetails(String email) {

        OnboardingDetails onboardingDetails = new OnboardingDetails();

        Employee employee = employeeRepository.findByEmail(email);
        if (employee == null)
            throw new NotFoundException("Employee not found with email: " + email);

        int employeeId = employee.getEmployeeId();

        fetchOnboardingDetails(onboardingDetails, employeeId, "personalDetails");
        fetchOnboardingDetails(onboardingDetails, employeeId, "contact");
        fetchOnboardingDetails(onboardingDetails, employeeId, "education");
        fetchOnboardingDetails(onboardingDetails, employeeId, "professional");
        fetchOnboardingDetails(onboardingDetails, employeeId, "additional");

        String firstName = employee.getFirstName() == null ? "" : employee.getFirstName() + " ";
        String middleName = employee.getMiddleName() == null ? "" : employee.getMiddleName() + " ";
        String lastName = employee.getLastName() == null ? "" : employee.getLastName();
        String fullName = firstName + middleName + lastName;
        fullName = fullName.trim();

        EmployeeDTO employeeDTO = new EmployeeDTO(fullName, employee.getMobileNumber(), employee.getDateOfBirth(),
                employee.getEmail());

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(onboardingDetails);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int sizeInBytes = json.getBytes(StandardCharsets.UTF_8).length;

        System.out.println("Sizeeeeeeeeeeeeeeeeeeeeeeeeee: " + sizeInBytes + " bytes");

        return new PreviewResponseDTO(onboardingDetails, employeeDTO, employee.getStatus());
    }

    @Override
    public String updateEmployeeStatus(String email, String status) {

        Employee employee = employeeRepository.findByEmail(email);
        employee.setStatus(status);
        employeeRepository.save(employee);

        return "For employee " + email + ", status is updated from PENDING to " + status + " succeesully.";
    }

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public GeneralResponse notifyAdmin(String email) {

        emailService.sendEmail(email, null,
                EmailConstant.VERIFY_EMPLOYEE_DETAILS_SUBJECT, EmailConstant.VERIFY_EMPLOYEE_DETAILS_TEMPLATE, email);

        return new GeneralResponse("An email is successfully sent to Admin to verify employee " + email + " details.");
    }

}
