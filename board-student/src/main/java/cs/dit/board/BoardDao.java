package cs.dit.board;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class BoardDao {

	/**======================================================================
	 * 패키지명 : cs.dit.board
	 * 파일명   : BoardDao.java
	 * 작성자  : 
	 * 변경이력 : 
	 *   2022-9-11
	 * 프로그램 설명 : board 테이블의 내용과 연동하여 게시글 관리
	*======================================================================*/

	//전역변수 sqlMapper(SqlSessionFactory) 선언
	private static SqlSessionFactory sqlMapper = null;
	
	//sqlMapper를 생성하는 getInstance() 선언 SqlSessionFactory 사용 
	public static SqlSessionFactory getInstance() {
		if(sqlMapper==null) {
			if(sqlMapper==null) {
				try {
					InputStream inputStream = Resources.getResourceAsStream("cs/dit/board/board-config.xml");
					sqlMapper = new SqlSessionFactoryBuilder().build(inputStream);
					inputStream.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			return sqlMapper;
		}
	}
	
	//list(page, numOfRecords)
	public List<BoardDto> list(int page, int numOfRecords){
		sqlMapper = getInstance();
		
		SqlSession session = sqlMapper.openSession();
		Map<String, Object> param = new HashMap<>();
		param.put("page", (page-1)*numOfRecords);
		param.put("numOfRecords", numOfRecords);
		
		System.out.println("sqlMapper 사용!!!");
		try {
			return session.selectList("cs.dit.board.selectAll", param);
		}finally {
			session.close();
		}
	}
	
	private Connection getConnection() throws Exception{
		
		Context initCtx  = new InitialContext();//1. JNDI를 이용하기 위한 객체 생성
		
		Context envCtx = (Context) initCtx.lookup("java:comp/env");//2. 등록된 네이밍 서비스로부터 등록된 자원을 가져옴
		
		DataSource ds = (DataSource) envCtx.lookup("jdbc/jskim");//3. 자원들 중 원하는 jdbc/jskim 자원을 찾아내어 데이터소스를 가져옴
		
		Connection con = ds.getConnection();//4. 커넥션 얻어옴
		
		return con;
	}
	
	public void insert(BoardDto dto) {
		String sql = "INSERT INTO board(SUBJECT, CONTENT, WRITER, REGDATE, FILENAME) VALUES(?, ?, ?, SYSDATE(), ?)";
		
		try (
			Connection con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
		)
		{   
			pstmt.setString(1,  dto.getSubject());
			pstmt.setString(2,  dto.getContent());
			pstmt.setString(3,  dto.getWriter());
			pstmt.setString(4,  dto.getFilename());
			
			//1. insert 문을 실행하는 코드를 작성하세요
			pstmt.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public ArrayList<BoardDto> list(int p, int numOfRecords){
//		String sql = "SELECT BCODE, SUBJECT, CONTENT, WRITER, REGDATE FROM board "
//				+ " order by bcode LIMIT ?, ?";
//		ArrayList<BoardDto> dtos = new ArrayList<BoardDto>();
//		
//		try (	Connection con = getConnection();
//				PreparedStatement pstmt = con.prepareStatement(sql);
//				
//			)
//			{
//				pstmt.setInt(1, (p-1)*numOfRecords);
//				pstmt.setInt(2, numOfRecords);
//				
//				ResultSet rs = pstmt.executeQuery();
//				while(rs.next()) {
//					BoardDto dto = new BoardDto();
//					
//					dto.setBcode(rs.getInt("bcode"));
//					dto.setSubject(rs.getString("subject"));
//					dto.setContent(rs.getString("content"));
//					dto.setWriter(rs.getString("writer"));
//					dto.setRegDate(rs.getDate("regDate"));
//					
//					//2. 위에서 만들어진 dto를 ArrayList 인 dtos에 차례로 입력하세요.
//					dtos.add(dto);
//					
//				}
//				rs.close();
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		return dtos;
//	}
	
	public BoardDto selectOne(int bcode) {
		sqlMapper = getInstance();
		SqlSession session = sqlMapper.openSession();
		try {
			return session.selectOne("cs.dit.board.seletOne", bcode);
		}finally {
			session.close();
		}
	}
//		//3. 전달받은 bcode를 가진 레코드를 검색하는 select 문을 아래에 작성하세요.
//		String sql = "select * from board where bcode=?";
//		
//		
//		BoardDto dto = new BoardDto();
//		
//		try (	Connection con = getConnection();
//				PreparedStatement pstmt = con.prepareStatement(sql);
//				)
//		{	pstmt.setInt(1, bcode);
//		
//			try(ResultSet rs = pstmt.executeQuery();)
//			{
//				rs.next();
//				
//				dto.setBcode(bcode);
//				dto.setSubject(rs.getString("subject"));
//				dto.setContent(rs.getString("content"));
//				dto.setWriter(rs.getString("writer"));
//				dto.setRegDate(rs.getDate("regDate"));
//				dto.setFilename(rs.getString("filename"));
//				
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return dto;
//	}
	
	public void update(BoardDto dto) {
		String sql = "UPDATE board SET subject = ?, content = ?, writer = ?, regDate = ? WHERE bcode =?";
		
		try (
			Connection con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
		)
		{
			pstmt.setString(1,  dto.getSubject());
			pstmt.setString(2,  dto.getContent());
			pstmt.setString(3,  dto.getWriter());
			pstmt.setDate(4,  (Date) dto.getRegDate());
			pstmt.setInt(5, dto.getBcode());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void delete(int bcode) {
		String sql = "DELETE FROM board WHERE bcode =?";
		
		try (
			Connection con = getConnection();
			PreparedStatement pstmt = con.prepareStatement(sql);
		)
		{
			pstmt.setInt(1, bcode);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public int recordCount() {
		sqlMapper  = getInstance();
		SqlSession session = sqlMapper.openSession();
		try {
			return session.selectOne("cs.dit.board.recordCount");
		}finally {
			session.close();
		}
		
	}
//		int count =0;
//		
//		String sql = "select count(bcode) from board";
//		
//		
//		try (	Connection con = getConnection();
//				Statement stmt = con.createStatement();	
//				ResultSet rs = stmt.executeQuery(sql);)
//		{			
//			if(rs.next()) {
//				count = rs.getInt(1);
//			}
//			System.out.println(count);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//		
//		return count;
//		
//	}
		
}








