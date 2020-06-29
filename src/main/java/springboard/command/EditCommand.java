package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

@Service
public class EditCommand implements BbsCommandImpl {
	
	JDBCTemplateDAO dao;
	@Autowired
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동주입(Edit)");
	}
	
	@Override
	public void execute(Model model) {
		
		//파라미터 한번에 받기
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		String idx = req.getParameter("idx");
		
//		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		SpringBbsDTO dto = dao.view(idx);
		model.addAttribute("viewRow", dto);
		dao.close();		
	}
	
}
