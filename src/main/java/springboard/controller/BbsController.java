package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import model.JdbcTemplateConst;
import springboard.command.BbsCommandImpl;
import springboard.command.ListCommand;

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
	
}


























