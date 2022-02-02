package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {

	private Connection conn;
	private ResultSet rs;
	
	
	public BbsDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost:3305/BBS?serverTimezone=UTC";
			String dbID = "root";
			String dbPassword = "!jy101712";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection(dbURL, dbID, dbPassword);
			
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	//현재 날짜를 가져오는 함수
	public String getDate() {
		
		String SQL = "SELECT NOW()";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		return "";	//데이터베이스 오류		
	}
	
	//게시글 번호(마지막 가져온 번호+1)
	public int getNext() {
		
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getInt(1) + 1;
			}
			return 1;//초기 게시글 번호 위치 알려줌 - 첫 번째 게시글일 경우 1
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		return -1;	//데이터베이스 오류		
	}
	
	//글을 작성하는 함수
	public int write(String bbsTitle, String userID, String bbsContent) {		
		String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1); //글이 삭제가 안된 상태
			return pstmt.executeUpdate();
		}catch(Exception e) {		
			e.printStackTrace();
		}
		return -1;	//데이터베이스 오류	
	}
	
	//10개 이상 페이지 처리하는 함수.
	public ArrayList<Bbs> getList(int pageNumber){
		String SQL = "SELECT *FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10";
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber -1)*10);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				list.add(bbs);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;	//데이터베이스 오류		
	}
	
	//10개 미만 페이지 처리하는 함수.
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT *FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 ";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber -1)*10);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;	
	}
	
	//게시글 클릭했을때 내용을 불러오는 함수.
	public Bbs getBbs(int bbsID) {
		String SQL = "SELECT *FROM BBS WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			if(rs.next()){
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				return bbs;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	//게시글 수정 함수
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID); //글이 삭제가 안된 상태
			return pstmt.executeUpdate();
		}catch(Exception e) {		
			e.printStackTrace();
		}
		return -1;	//데이터베이스 오류	
		
	}
	
	//게시글 삭제 함수
		public int delete(int bbsID) {
			String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?";
			try {
				PreparedStatement pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, bbsID);
				return pstmt.executeUpdate();
			}catch(Exception e) {		
				e.printStackTrace();
			}
			return -1;	//데이터베이스 오류	
			
		}
	
}
