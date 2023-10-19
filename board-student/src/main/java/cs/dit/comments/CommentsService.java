package cs.dit.comments;






import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

public class CommentsService {

	CommentsDao dao = new CommentsDao(); //아래의 메소드에서 공통으로 사용하는 객체
	//데이터베이스에서 한줄 댓글을 가져와서 json으로 ajax에 전달
	public void listCommets(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		int bcode = Integer.parseInt(request.getParameter("bcode"));
		response.setContentType("application/x-json;charset=utf-8");
		
		JSONArray list = new JSONArray();
		
		list = dao.listComments(bcode);
		
		response.getWriter().print(list);//ajax에게 전달
	}
	
	public void insertComments(HttpServletRequest request, HttpServletResponse reponse)
			throws ServletException, IOException {
		
		int bcode = Integer.parseInt(request.getParameter("bcode"));
		
		String comments = request.getParameter("comments");
		
		CommentsDto dto = new CommentsDto(0, 0, bcode, comments, null);//db에 입력할 dto객체 생성
		
		dao.insertComments(dto);
	}
}
