package com.kodlamaio.hrms.business.conretes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.kodlamaio.hrms.business.abstracts.JobSeekerService;

import com.kodlamaio.hrms.core.mailvalidate.MailValidService;
import com.kodlamaio.hrms.core.mailvalidation.MailValidationService;
import com.kodlamaio.hrms.core.mernis.PersonValidationService;
import com.kodlamaio.hrms.core.register.Register;
import com.kodlamaio.hrms.core.utilities.result.DataResult;
import com.kodlamaio.hrms.core.utilities.result.ErrorResult;
import com.kodlamaio.hrms.core.utilities.result.Result;
import com.kodlamaio.hrms.core.utilities.result.SuccessDataResult;
import com.kodlamaio.hrms.core.utilities.result.SuccessResult;
import com.kodlamaio.hrms.dataAccess.abstracts.CvDao;
import com.kodlamaio.hrms.dataAccess.abstracts.JobPositionDao;
import com.kodlamaio.hrms.dataAccess.abstracts.JobSeekerDao;
import com.kodlamaio.hrms.entities.conretes.Cv;
import com.kodlamaio.hrms.entities.conretes.EMailVerification;
import com.kodlamaio.hrms.entities.conretes.JobSeeker;
import com.kodlamaio.hrms.entities.conretes.Language;
import com.kodlamaio.hrms.entities.conretes.ProgrammingLanguageOrTechnology;
import com.kodlamaio.hrms.entities.conretes.School;
import com.kodlamaio.hrms.entities.conretes.WorkExperience;

@Service
public class JobSeekerManager implements JobSeekerService {

	JobSeekerDao jobSeekerDao;
	CvDao cvDao;
	JobPositionDao jobPositionDao;
	PersonValidationService personValidation;
	MailValidService mailValidService;
	MailValidationService mailValidationService;
	
	

	@Autowired
	public JobSeekerManager(JobSeekerDao jobSeekerDao, PersonValidationService personValidation,
			MailValidService mailValidService, MailValidationService mailValidationService,
			CvDao cvDao,JobPositionDao jobPositionDao) {
		this.jobSeekerDao = jobSeekerDao;
		this.personValidation = personValidation;
		this.mailValidService = mailValidService;
		this.mailValidationService = mailValidationService;
		this.cvDao=cvDao;
		this.jobPositionDao=jobPositionDao;
	}

	@Override
	public DataResult<List<JobSeeker>> getall() {
		return new SuccessDataResult<List<JobSeeker>>(jobSeekerDao.findAll(), "Data Listelendi");

	}

	@Override
	public Result add(@RequestBody JobSeeker jobSeeker) {
		boolean nameIsEmpty = true;
		boolean lastnameIsEmpty = true;
		boolean nationalIdentityIsEmpty = true;
		boolean nationalIdentityisValid = false;
		boolean nationalIdentityIsUsed = true;
		boolean dateOfBirthIsEmpty = true;
		boolean eMailIsEmpty = true;
		boolean eMailIsUsed = true;
		boolean mailIsValid = false;
		boolean passwordIsEmpty = true;
		boolean passwordsSame = false;

		String error = "";

		jobSeeker = Register.normalizeJobSeeker(jobSeeker);
		if (!jobSeeker.getName().isEmpty()) {
			nameIsEmpty = false;
		} else {
			error += " İsim boş olamaz.";
		}

		if (!jobSeeker.getLastName().isEmpty()) {
			lastnameIsEmpty = false;
		} else {
			error += " Soyad boş olamaz.";
		}

		if (!jobSeeker.getNationalIdentity().isEmpty()) {
			nationalIdentityIsEmpty = false;
			if (jobSeeker.getNationalIdentity().length() == 11) {
				nationalIdentityisValid = true;
				if (jobSeekerDao.findByNationalIdentity(jobSeeker.getNationalIdentity()) == null) {
					nationalIdentityIsUsed = false;
				} else {
					error += " TC Kimlik numarası zaten kullanılıyor";
				}
			} else {
				error += " TC Kimlik numarası 11 hane olmalıdır.";
			}
		} else {
			error += " TC Kimlik numarası boş olamaz.";
		}

		if (jobSeeker.getBirthDay() != null) {
			dateOfBirthIsEmpty = false;
		} else {
			error += " Doğum tarihi boş olamaz.";
		}

		if (!jobSeeker.getMember().getEMail().isEmpty()) {
			eMailIsEmpty = false;
			if (mailValidService.mailIsValid(jobSeeker.getMember().getEMail())) {
				mailIsValid = true;

				if (jobSeekerDao.findByMember_eMail(jobSeeker.getMember().getEMail()) == null) {
					eMailIsUsed = false;

				} else {
					error += " e-mail adresi zaten kullanılıyor.";
				}

			} else {
				error += " Geçersiz e-mail adresi";
			}

		} else {
			error += " e-mail adresi boş olamaz.";
		}

		if (!jobSeeker.getMember().getPassword().isEmpty() && !jobSeeker.getMember().getPasswordRepeat().isEmpty()) {
			passwordIsEmpty = false;
			if (jobSeeker.getMember().getPassword().equals(jobSeeker.getMember().getPasswordRepeat())) {
				passwordsSame = true;

			} else {
				error += " Şifre ve şifre tekrar eşleşmiyor";
			}
		} else {
			error += " Şifre ve şifre tekrar alanları boş olamaz";
		}

		/*
		 * System.out.println("Name "+nameIsEmpty+ " "+ "Last name "+lastnameIsEmpty+
		 * " nationalIdentityIsEmpty "+nationalIdentityIsEmpty+
		 * " nationalIdentiyisValid "+nationalIdentiyisValid+
		 * " dateOfBirthIsEmpty "+dateOfBirthIsEmpty+ " eMailIsEmpty "+eMailIsEmpty+
		 * " eMailIsUsed "+eMailIsUsed+ " passwordIsEmpty "+passwordIsEmpty +
		 * " passwordsSame "+passwordsSame + " mailIsValid "+mailIsValid);
		 */

		if (!nameIsEmpty && !lastnameIsEmpty && !nationalIdentityIsEmpty && !dateOfBirthIsEmpty
				&& nationalIdentityisValid && !nationalIdentityIsUsed && !passwordIsEmpty && passwordsSame
				&& !eMailIsUsed && !eMailIsEmpty && mailIsValid) {
			if (personValidation.validate(Long.valueOf(jobSeeker.getNationalIdentity()),
					jobSeeker.getName().toUpperCase(), jobSeeker.getLastName().toUpperCase(),
					jobSeeker.getBirthDay().getYear())) {

				
				if(jobSeeker.getCv()!=null&&jobSeeker.getCv().getWorkExperience()!=null) {
				List<WorkExperience> experiences = new ArrayList<WorkExperience>();
				
				for(WorkExperience workExperience :jobSeeker.getCv().getWorkExperience()) {
					
					
					workExperience.setCv(jobSeeker.getCv());
					
					workExperience.setJobPosition(jobPositionDao.findById(workExperience.getJobPosition().getId()));
					
					experiences.add(workExperience);
				}
				jobSeeker.getCv().setWorkExperience(experiences);
				}
								
				jobSeeker.setEMailVerification(new EMailVerification());
				jobSeeker.getEMailVerification().setMailVerified(false);
				
				jobSeekerDao.save(jobSeeker);
				mailValidationService.sendMailValidation(jobSeeker.getMember().getEMail());
				return new SuccessResult("İş arayan eklendi");
			} else {
				error += " Girilen bilgiler gerçek bir insana ait değil.";
				return new ErrorResult(error);
			}
		} else {
			
			return new ErrorResult(error);
		}

	} 

	@Override
	public Result addCv(int jobSeekerId, Cv cv) {

		JobSeeker jobSeeker = jobSeekerDao.findById(jobSeekerId);
		
		
		
		
		if(jobSeeker.getCv()==null) {
			jobSeeker.setCv(new Cv());
			jobSeeker.getCv().setJobSeeker(jobSeeker);
		}
			
		
			if(cv.getWorkExperience()!=null) {
			
				List<WorkExperience> experiences = new ArrayList<WorkExperience>();
				
				for(WorkExperience workExperience :cv.getWorkExperience()) {
					
					
					workExperience.setCv(jobSeeker.getCv());
					
					workExperience.setJobPosition(jobPositionDao.findById(workExperience.getJobPosition().getId()));
					
					experiences.add(workExperience);
				}
				
			if(jobSeeker.getCv().getWorkExperience()==null) {
				
				for(WorkExperience workExperience:cv.getWorkExperience()) {
					workExperience.setCv(jobSeeker.getCv());
				}
				jobSeeker.getCv().setWorkExperience(cv.getWorkExperience());
			}else {
				
				List<WorkExperience> exWorkExperiences = jobSeeker.getCv().getWorkExperience();
				
				for(WorkExperience experience:experiences) {
					experience.setCv(jobSeeker.getCv());
					exWorkExperiences.add(experience);
				}
				jobSeeker.getCv().setWorkExperience(exWorkExperiences);
			}
			
			}
			
		if(cv.getDescription()!=null) {
			jobSeeker.getCv().setDescription(cv.getDescription());
		}
		
		if(cv.getGithubAdress()!=null) {
			jobSeeker.getCv().setGithubAdress(cv.getGithubAdress());
		}
		
		if(cv.getLinkedinAdress()!=null) {
			jobSeeker.getCv().setLinkedinAdress(cv.getLinkedinAdress());
		}
		
		if(cv.getLanguage()!=null) {
			
			if(jobSeeker.getCv().getLanguage()!=null) {
			List<Language> exLanguages = jobSeeker.getCv().getLanguage();
			
			for(Language language: cv.getLanguage()) {
				language.setCv(jobSeeker.getCv());
				exLanguages.add(language);
			}
			jobSeeker.getCv().setLanguage(exLanguages);
			
			}else {
				
				for(Language language:cv.getLanguage()) {
					language.setCv(jobSeeker.getCv());
				}
			
				jobSeeker.getCv().setLanguage(cv.getLanguage());
			}
			
		}
		
		if(cv.getProgrammingLanguageOrTechnology()!=null) {
			
			if(jobSeeker.getCv().getProgrammingLanguageOrTechnology()!=null) {
				List<ProgrammingLanguageOrTechnology> exList = jobSeeker.getCv().getProgrammingLanguageOrTechnology();
				
				for(ProgrammingLanguageOrTechnology program : cv.getProgrammingLanguageOrTechnology()) {
					program.setCv(jobSeeker.getCv());
					exList.add(program);
				}
				jobSeeker.getCv().setProgrammingLanguageOrTechnology(exList);
			}else {
				for(ProgrammingLanguageOrTechnology program : cv.getProgrammingLanguageOrTechnology()) {
					program.setCv(jobSeeker.getCv());
				}
				jobSeeker.getCv().setProgrammingLanguageOrTechnology(cv.getProgrammingLanguageOrTechnology());
			}
		}
		
		if(cv.getSchool()!=null) {
			if(jobSeeker.getCv().getSchool()!=null) {
				List<School> exSchools = jobSeeker.getCv().getSchool();
				
				for(School school:cv.getSchool()) {
					school.setCv(jobSeeker.getCv());
					exSchools.add(school);
				}
				jobSeeker.getCv().setSchool(exSchools);
			}
		}
		
		jobSeekerDao.save(jobSeeker);
		
		return new SuccessResult("Cv eklendi.");
	}

	public DataResult<Cv> getCv(int jobSeekerId){
		
		JobSeeker jobSeeker = jobSeekerDao.findById(jobSeekerId);
		
		return new SuccessDataResult<Cv>(jobSeeker.getCv());
		
		
	}
	
	public DataResult<JobSeeker> getJobSeekerById(int id){
		return new SuccessDataResult<JobSeeker>(jobSeekerDao.findById(id),"İş Arayan Getirildi");
	}
	
}
