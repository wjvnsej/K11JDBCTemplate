package springboard.command;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;
import springboard.util.EnvFileReader;
import springboard.util.PagingUtil;

/*
BbsCommandImpl를 구현하였으므로 execute()메소드는 반드시 오버라이딩 해야한다.
또한 해당 객체는 부모타입인 BbsCommandImpl로 참조할 수 있다.
 */

/*
Service 역할의 클래스임을 명시함
Service객체는 Controller와 Model사이에서 중재역할을 함
*/
@Service
public class ListCommand implements BbsCommandImpl {
	
	JDBCTemplateDAO dao;
	@Autowired
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동주입(List)");
	}
	
	/*
	컨트롤러에서 인자로 전달해준 model객체를 매개변수로 전달받는다.
	model객체에는 사용자가 요청한 정보인 request객체가 저장되어 있다.
	*/
	@Override
	public void execute(Model model) {
		
		System.out.println("ListCommand > execute() 호출");
		
		
		/*
		컨트롤러에서 넘겨준 파라미터를 asMap() 메소드를 통해 Map컬렉션으로 변환한다.
		그리고 request객체를 형 변환하여 가져온다.
		*/
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//DAO객체생성(1차버전)
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		//검색어 관련 폼값 처리
		String addQueryString = "";
		String searchColumn = req.getParameter("searchColumn");
		String searchWord = req.getParameter("searchWord");
		if(searchWord != null) {
			addQueryString = String.format("searchColumn = %s" + "&searchWord = %s", 
					searchColumn, searchWord);
			
			paramMap.put("Column", searchColumn);
			paramMap.put("Word", searchWord);
		}
		
		//전체 레코드 수 카운트하기
		int totalRecordCount = dao.getTotalCount(paramMap);
		
		///////////////////////////////////////////////////////////
		//페이지 처리부분 Start
		
		//Environment객체를 이용한 외부파일 읽어오기
		int pageSize = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties", "SpringBoard.pageSize"));
		int blockPage = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties", "SpringBoard.blockPage"));
		
		//전체페이지 수 계산
		int totalPage = (int)Math.ceil((double)totalRecordCount / pageSize);
		
		//현재 페이지 번호. 첫 진입이라면 무조건 1페이지로 지정
		int nowPage = req.getParameter("nowPage") == null ? 1 : Integer.parseInt(req.getParameter("nowPage"));
		
		//리스트에 출력 할 게시물의 시작/종료 구간(select절의 between에
		int start = (nowPage - 1) * pageSize + 1;
		int end = nowPage * pageSize;
		
		paramMap.put("start", start);
		paramMap.put("end", end);
		
		//페이지 처리부분 End
		///////////////////////////////////////////////////////////
		
		//출력할 리스트 가져오기
		//ArrayList<SpringBbsDTO> listRows = dao.list(paramMap); //페이지X
		ArrayList<SpringBbsDTO> listRows = dao.listPage(paramMap); //페이지O
		
		//가상번호 계산하여 부여하기
		int virtualNum = 0;
		int countNum = 0;
		
		for(SpringBbsDTO row : listRows) {
			
			//전체게시물의 갯수에서 하나씩 차감하면서 가상번호 부여
			//virtualNum = totalRecordCount --;
			
			//페이지번호 적용하여 가상번호 계산
			virtualNum = totalRecordCount - (((nowPage - 1) * pageSize) + countNum++);
			row.setVirtualNum(virtualNum);
			
			//답변글에 대한 리스트 처리(re.gif 이미지를 제목에 삽입)
			String reSpace = "";
			//해당 게시물의 indent가 0보다 크다면(답변글이라면)...
			if(row.getBindent() > 0) {
				//indent의 크기만큼 공백(&nbsp;)을 추가해준다.
				for(int i = 0; i < row.getBindent(); i++) {
					reSpace += "&nbsp;&nbsp;";
				}
				//reply이미지를 추가해준다.
				row.setTitle(reSpace + "<img src='../images/re3.gif'>" + row.getTitle());
			}
		}
		
		//model객체에 출력리스트 저장
		String path = req.getContextPath() + "/board/list.do?" + addQueryString;
		String pagingImg = PagingUtil.pagingImg(totalRecordCount, pageSize, blockPage, nowPage, path);
		model.addAttribute("pagingImg", pagingImg);
		model.addAttribute("totalPage", totalPage); //전체페이지수
		model.addAttribute("nowPage", nowPage);		//현재페이지번호
		model.addAttribute("listRows", listRows);
		
		//JDBCTemplate에서는 자원반납을 하지 않는다.
		//dao.close();
		
	}
	
}
