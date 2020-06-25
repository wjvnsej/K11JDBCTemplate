package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import model.JdbcTemplateConst;
import springboard.command.BbsCommandImpl;
import springboard.command.ListCommand;
import springboard.command.ViewCommand;
import springboard.command.WriteActionCommand;
import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

/*
@Autowired
	: 스프링 설정파일에서 생성된 빈을 자동으로 주입받을 때
	사용하는 어노테이션
	- 생성자, 필드(멤버변수), 메소드(setter)에 적용가능
	- setXXX()의 형식이 아니어도 적용가능
	- 타입을 이용해 자동으로 프로퍼티의 값을 설정
	- 따라서 빈을 주입받을 객체가 존재하지 않거나, 같은 타입이
	2개이상 존재하면 예외가 발생됨
*/
@Controller
public class BbsController {
	
	private JdbcTemplate template;
	/*
	스프링 어플리케이션이 구동될 때 미리 생성된 JdbcTemplate타입의
	빈을 자동으로 주입받게 된다.
	*/
	@Autowired
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
		System.out.println("@Autowired -> JDBCTemplate 연결성공!");
		
		JdbcTemplateConst.template = this.template;
	}
	
	/*
	BbsCommandImpl 타입의 멤버변수 선언
	멤버변수이므로 클래스 내에서 전역적으로 사용한다. 해당 클래스의
	모든 command객체는 위 인터페이스를 구현하여 정의하게 된다.
	*/
	BbsCommandImpl command = null;
	//게시판 리스트
	@RequestMapping("/board/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		/*
		사용자로부터 받은 모든 요청은 HttpServletRequest객체에 저장되고
		이를 커맨드 객체로 전달하기 위해 model에 저장 후 매개변수로 전달한다.
		*/
		model.addAttribute("req", req);
		/*
		컨트롤러는 사용자의 요청을 분석한 후 해당 요청에 맞는 서비스 객체만 호출하고,
		실제 DAO의 호출이나 비지니스 로직은 아래 command객체가 처리하게 된다.
		*/
		command = new ListCommand();
		command.execute(model);
		
		return "07Board/list";
	}
	
	@RequestMapping("/board/write.do")
	public String write(Model model) {
		
		return "07Board/write";
	}
	
	/*
	글쓰기처리 : post로 전송되므로 두가지 속성을 모두 사용하여 매핑
	*/
	@RequestMapping(value = "/board/writeAction.do", method = RequestMethod.POST)
	public String writeAction(Model model, HttpServletRequest req, SpringBbsDTO springBbsDTO) {
		
		//request객체를 모델에 저장
		model.addAttribute("req", req);
		//View에서 전송한 폼값을 한꺼번에 저장한 커맨드객체로 DTO를 저장
		model.addAttribute("springBbsDTO", springBbsDTO);
		command = new WriteActionCommand();
		command.execute(model);
		
		//글쓰기 처리 완료 후 list.do로 로케이션(이동) 된다.
		return "redirect:list.do?nowPage=1";
	}
	
	@RequestMapping("/board/view.do")
	public String view(Model model, HttpServletRequest req) {
		
		model.addAttribute("req", req);
		command = new ViewCommand();
		command.execute(model);
		
		return "07Board/view";
	}
	
	@RequestMapping("/board/password.do")
	public String password(Model model, HttpServletRequest req) {
		
		model.addAttribute("idx", req.getParameter("idx"));
		return "07Board/password";
	}
	
	@RequestMapping("/board/passwordAction.do")
	public String passwordAction(Model model, HttpServletRequest req) {
		
		String modePage = null;
		
		String mode = req.getParameter("mode");
		String idx= req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		String pass = req.getParameter("pass");
		
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		int rowExist = dao.password(idx, pass);
		
		if(rowExist <= 0) {
			model.addAttribute("isCorrMsg", "패스워드가 일치하지 않습니다.");
			model.addAttribute("idx", idx);
			modePage = "07Board/password";
		}
		else {
			System.out.println("검증완료");
		}
		return modePage;
	}
	
}


























