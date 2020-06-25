package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

public class ViewCommand implements BbsCommandImpl {
	
	@Override
	public void execute(Model model) {
		
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//request객체를 통해 폼값을 받음
		String idx = req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		
		//DAO의 view메소드를 호출하여 idx에 해당하는 레코드 하나를 반환받음
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		SpringBbsDTO dto = new SpringBbsDTO();
		dto = dao.view(idx);
		//내용에 대해 줄바꿈 처리
		dto.setContents(dto.getContents().replace("\r\n", "<br>"));
		
		model.addAttribute("viewRow", dto);
		model.addAttribute("nowPage", nowPage);
		
	}

}
