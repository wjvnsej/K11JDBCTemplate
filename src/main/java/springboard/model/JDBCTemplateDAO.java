package springboard.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import model.JdbcTemplateConst;

/*
JdbcTemplate 관련 주요메소드

- List query(String sql, RowMapper rowMapper)
	: 여러개의 레코드를 반환하는 select 계열의 쿼리문인 경우 사용한다.
- List query(String sql, Object[] args, RowMapper rowMapper)
	: 인파라미터를 가진 여러개의 레코드를 반환하는 select 계열의 쿼리문인 경우 사용한다.
	
- int queryForInt(String sql) 혹은 queryForInt(String sql, object[] args)
	: 쿼리문의 실행결과가 숫자를 반환하는 select 계열의 쿼리문에 사용한다.
- Object queryForObject(String sql, RowMapper rowMapper)
	혹은 Object queryForObject(String sql, Object[] args, RowMapper rowmapper)
	: 하나의 레코드를 반환하는 select 계열의 쿼리문 실행 시 사용된다.

- int update(String sql)
	: 인파라미터가 없는 update/delete/insert 쿼리문을 처리할 때 사용함
- int update(String sql, Object[] args)
	: 인파리미터가 있는 update/delete/insert 쿼리문을 처리할 때 사용함
*/
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
	
	//게시판 리스트(페이지처리X)
	public ArrayList<SpringBbsDTO> list(Map<String, Object> map){
		
		String sql = " "
				+ "SELECT * FROM springboard ";
			if(map.get("Word") != null) {
				sql += "	WHERE " + map.get("Column") + " "
					+ "		LIKE '%" + map.get("Word") + "%' ";
			}
			sql += "	ORDER BY idx DESC";
			
		/*
			query메소드의 반환타입은 List계열의 컬렉션이므로 제네릭부분만
			우리가 필요한 DTO객체로 대체하면 된다. 나머지는 RowMapper객체가
			모두 알아서 처리해준다.
		*/
		return (ArrayList<SpringBbsDTO>)template
				.query(sql, new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
	}
	
	//글쓰기 처리1
	public void write(final SpringBbsDTO springBbsDTO) {
		
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				/*
				답변형 게시판에서 원본글인 경우에는 idx(일련번호)와
				bgroup(그룹번호)가 반드시 일치해야 한다.
				또한 nextVal은 한 문장에서 여러번 사용하더라도 같은 시퀀스를
				반환한다.
				*/
				String sql = "INSERT INTO springboard ("
					+ " idx, name, title, contents, hits "
					+ ", bgroup, bstep, bindent, pass) "
					+ "VALUES ( "
					+ "springboard_seq.NEXTVAL, ?, ?, ?, 0, "
					+ "springboard_seq.NEXTVAL, 0, 0, ?)";
				
				PreparedStatement psmt = con.prepareStatement(sql);
				psmt.setString(1, springBbsDTO.getName());
				psmt.setString(2, springBbsDTO.getTitle());
				psmt.setString(3, springBbsDTO.getContents());
				psmt.setString(4, springBbsDTO.getPass());
				
				return psmt;
				
			}
		});
	}
	
	//조회수 증가
	public void updateHit(final String idx) {
		
		String sql = "UPDATE springboard SET "
				+ "	hits = hits + 1 "
				+ " WHERE idx = ? ";
		
		/*
		매개변수로 전달되는 idx를 아래 익명클래스 내부에서 사용하기 위해서는
		반드시 final로 선언해야 사용이 가능하다.
		(자바의 규칙)
		*/
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(idx));
			}
		});
	}
	
	//상세보기 처리
	public SpringBbsDTO view(String idx) {
		
		//조회수 증가
		updateHit(idx);
		
		SpringBbsDTO dto = new SpringBbsDTO();
		String sql = "SELECT * FROM springboard "
				+ "	WHERE idx = " + idx;
		/*
		queryForObject()메소드는 반환결과가 0개이거나 2개이상인
		경우 예외가 발생하므로 반드시 예외처리를 해주는것이 좋다.
		*/
		try {
			dto = template.queryForObject(sql, 
					new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
		} 
		catch (Exception e) {
			System.out.println("View() 실행시 예외발생");
		}
		return dto;
	}
	
	public int password(String idx, String pass) {
		
		int retNum = 0;
		String sql = "SELECT * FROM springboard "
				+ "	WHERE pass = " + pass + "	AND idx = " + idx;
		
		/*
		만약 패스워드가 틀린 경우라면 반환되는 형이 0개이므로
		예외처리를 하고있다. queryForObject()는 반환되는 형이
		1개일때만 정상작동한다.
		*/
		try {
			SpringBbsDTO dto = template.queryForObject(sql, 
					new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
			retNum = dto.getIdx();
		} 
		catch (Exception e) {
			System.out.println("password() 예외발생");
		}
		return retNum;
	}
	
}


















