package springboard.model;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import model.JdbcTemplateConst;

public class JDBCTemplateDAO {
	
	//멤버변수
	JdbcTemplate template;
	
	public JDBCTemplateDAO(){
		/*
			컨트롤러에서 @Autowired를 통해 자동주입 받았던 빈을
			정적변수인 JdbcTemplateConst.template을 통해 가져온다.
			즉, DB연결정보를 웹어플리케이션 어디서든 사용할 수 있다.
		*/
		this.template = JdbcTemplateConst.template;
		System.out.println("JDBCTemplateDAO() 생성자 호출");
	}
	
	public void close() {
		//JDBCTemplate에서는 사용하지 않음
	}
	
	public int getTotalCount(Map<String, Object> map) {
		
		String sql = ""
				+ "SELECT COUNT(*) FROM springboard	";
			if(map.get("Word") != null) {
				sql += "	WHERE " + map.get("Column") + " "
					+ "			LIKE '%" + map.get("Word") + "%' ";
			}
			
			return template.queryForObject(sql, Integer.class);
	}
	
	public ArrayList<SpringBbsDTO> list(Map<String, Object> map){
		
		String sql = " "
				+ "SELECT * FROM springboard ";
			if(map.get("Word") != null) {
				sql += "	WHERE " + map.get("Column") + " "
					+ "		LIKE '%" + map.get("Word") + "%' ";
			}
			sql += "	ORDER BY idx DESC";
			
		return (ArrayList<SpringBbsDTO>)template.query(sql, new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
		
	}
	
}
