package cs.dit.board;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class BInsertService implements BoardService {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");

		//7. insertForm 에서 입력한 subject, content, writer 를 받아와 알맞는 변수에 입력하세요.
		
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		String writer = request.getParameter("writer");
		//System.out.println("insert service");
		String filename = null;
		String dir = null;
		
		String contentType = request.getContentType();
		if(contentType != null && contentType.toLowerCase().startsWith("multipart/")); {
			dir = request.getServletContext().getRealPath("/uploadfiles");//실제경로
		}
		
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdir();
		}
		
		
		Collection<Part> parts = request.getParts();
		
		for(Part p : parts) {
			
			if(p.getHeader("Content-Disposition").contains("filename=")) {
				if(p.getSize()>0) {
					filename = p.getSubmittedFileName();
					String filePath = dir + File.separator + filename;
					
					p.write(filePath);
					
					p.delete();
				}
			}
		}
		
		// String contentType = request.getContentType();
		// System.out.println(contentType); 
		
		BoardDto dto = new BoardDto(0, subject, content, writer, null, filename); 
		BoardDao dao = new BoardDao();
		dao.insert(dto);

	}

}
