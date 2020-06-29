package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

@Service
public class ReplyActionCommand implements BbsCommandImpl {
	
	JDBCTemplateDAO dao;
	@Autowired
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동주입(ReplyAction)");
	}
	
	@Override
	public void execute(Model model) {
		
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		SpringBbsDTO dto = (SpringBbsDTO)paramMap.get("springBbsDTO");
		
//		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		dao.reply(dto);
		//dao.close();
	}
	
}
