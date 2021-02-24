package tax.nalog.gov.by.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import tax.nalog.gov.by.utils.SpringConfig;
import tax.nalog.gov.by.entity.Admins;
import tax.nalog.gov.by.entity.Appeals;
import tax.nalog.gov.by.entity.Imns;
import tax.nalog.gov.by.form.PasswordForm;
import tax.nalog.gov.by.form.AppearDataForm;
import tax.nalog.gov.by.form.AppearIdForm;
import tax.nalog.gov.by.service.AdminsService;
import tax.nalog.gov.by.service.AppealsService;

@Controller
public class IndexController {
	
	private static final Logger logger = Logger.getLogger(IndexController.class);
	 @Autowired 
	 private HttpSession httpSession;
	
	public IndexController() {
		logger.info("IndexController");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		ctx.close();
	}
	
	@GetMapping("/")
	public ModelAndView indexViewGet(ModelMap modelMap) throws Exception{
		logger.info("indexViewGet");
		ModelAndView model = new ModelAndView("index");
		model.addObject("passwordForm", new PasswordForm());
		modelMap.addAttribute("loginfo", "");
		System.out.println(httpSession.getId());
		return model;
	}
	
	@PostMapping("/")
	public ModelAndView indexViewPost(@ModelAttribute("passwordForm")PasswordForm passwordForm, ModelMap model) throws Exception{
		logger.info("indexViewPost");
		
		AdminsService adminService = new AdminsService();
		Admins admin = adminService.findByLogin(passwordForm.getUsername());
		ModelAndView modelView;
		
		if (admin == null) {
			logger.info("admin is null");
			model.addAttribute("loginfo", "Неверный логин/пароль");
	    	modelView = new ModelAndView("index");
	    	return modelView;
		}
		
		boolean rez = passwordForm.equals(admin);
		
		if (rez == false) {
	    	model.addAttribute("loginfo", "Неверный логин/пароль");
	    	modelView = new ModelAndView("index");
	    	return modelView;
	    }
		
		logger.info("autorization:" + rez);
		httpSession.setAttribute("admin", admin);
	    modelView = new ModelAndView("main");
	    modelView.addObject("appearDataForm", new AppearDataForm());
	    modelView.addObject("appearIdForm", new AppearIdForm());
	    Imns imns = admin.getImns();
	    AppealsService appealsService = new AppealsService();
	    List<Appeals> listAppeals = null;
	    if (admin.getAccess() == 1) {
	    	listAppeals = appealsService.findAll();
	    }else {
	    	listAppeals = appealsService.getListByImns(imns);
	    }
	    modelView.addObject("imnsname", imns.getShotName());
	    modelView.addObject("appealsList", listAppeals);
		
		return modelView;
	}
	
	@GetMapping("/main")
	public ModelAndView mainViewGet(@ModelAttribute("appearIdForm") AppearIdForm appearIdForm, 
			ModelMap modelMap) throws Exception{
		  logger.info("mainVievGet");
		  
		  Admins admin = (Admins)httpSession.getAttribute("admin");		  
		  if (admin == null) {
			  return null;
		  }
		  
		  AppealsService appealsService = new AppealsService();
		  AppearDataForm appealDataForm = new AppearDataForm();
		  if (appearIdForm != null) { 
			  Appeals appleal = appealsService.findByID(appearIdForm.getId_fild());
			  appealDataForm.setByAppeal(appleal);
		  }
		  	  
		  ModelAndView modelView = new ModelAndView("main");
		  modelView.addObject("appearDataForm", appealDataForm);
		  modelView.addObject("appearIdForm", new AppearIdForm());
		  Imns imns = admin.getImns();
		  List<Appeals> listAppeals = null;
		  if (admin.getAccess() == 1) {
		    	listAppeals = appealsService.findAll();
		  }else {
		    	listAppeals = appealsService.getListByImns(imns);
		  }
		  modelView.addObject("imnsname", imns.getShotName());
		  modelView.addObject("appealsList", listAppeals);
		  
		  return modelView; 
	}
	
	
	  @PostMapping("/main") 
	  public ModelAndView mainViewPost(@ModelAttribute("appearDataForm") AppearDataForm appearDataForm, 
			  ModelMap modelMap) throws Exception{
		  logger.info("mainVievGet");
		  
		  Admins admin = (Admins)httpSession.getAttribute("admin");		  
		  if (admin == null) {
			  return null;
		  }
		  
		  AppealsService appealsService = new AppealsService();
		  appealsService.createEntity(appearDataForm, admin.getImns());
		  
		  ModelAndView modelView = new ModelAndView("main");
		  modelView.addObject("appearDataForm", new AppearDataForm());
		  modelView.addObject("appearIdForm", new AppearIdForm());
		  Imns imns = admin.getImns();
		  List<Appeals> listAppeals = null;
		  if (admin.getAccess() == 1) {
		    	listAppeals = appealsService.findAll();
		  }else {
		    	listAppeals = appealsService.getListByImns(imns);
		  }
		  modelView.addObject("imnsname", imns.getShotName());
		  modelView.addObject("appealsList", listAppeals);
		  
		  return modelView; 
	  }
	 
	
}
