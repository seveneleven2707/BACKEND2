package cs.dit.comments;

import java.sql.CallableStatement;
import java.sql.*;

import javax.naming.*;
import javax.sql.DataSource;

import org.json.simple.*;





public class CommentsDao {
	/**==========================================================
	 * 패캐지명 : cs.dit.board
	 * 파일명 : CommentsDao.java
	 * 작성자 : 방정철
	 * 변경이력 :
	 * 	2023-10-10/ 최초작성/ 방정철
	 * 프로그램 설명 : comments 테이블과 내용과 연동하여 한줄답변 관리
	 * 		- getConnection() : 커넥션풀에서 연결객체 얻디
	 * 		- listComments(int bcode)
	 * 		-insertComments(CommentsDto dto)
	 *===========================================================*/
	private Connection getConnection() throws Exception{
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource) envCtx.lookup("jdbc/jskim");
		Connection con = ds.getConnection();
		
		return con;
	}
	
	//Comments 관련 코드
	public JSONArray listComments(int bcode) {
		String sql = "select ccode, content, regdate from comments where bcode=? order by ccode desc";
		JSONArray list = new JSONArray();
		
		try (
			Connection con = getConnection();
			PrepareStatement pstmt = (PrepareStatement) con.prepareStatement(sql);)
		{	pstmt.setInt(1, bcode);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//JSON으로 저장 ・ [{"regdate"・2023-10-10, "rcode":1, "content":"저도 안녕"}]
				JSONObject json = new JSONObject();
				json.put("ccode", rs.getInt("ccode"));
				json.put("content", rs.getString("content"));
				String regdate = (rs.getDate("regdate")).toString();
				json.put("regdate", regdate);
				
				list.add(json);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void insertComments(CommentsDto dto) {
		String sql = "{call proc_comments(?, ?)}";
		
		try (
			Connection con = getConnection();
			CallableStatement cstmt = con.prepareCall(sql);
		)
		{	cstmt.setInt(1, dto.getBcode());    //SQL문과 데이터 바인딩
			cstmt.setString(2, dto.getContent());
			
			cstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
