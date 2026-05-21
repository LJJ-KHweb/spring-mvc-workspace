package com.kh.spring.board.model.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardMapper;
import com.kh.spring.board.model.dto.BoardDto;
import com.kh.spring.exception.AuthorizationException;
import com.kh.spring.exception.InvalidParameterException;
import com.kh.spring.member.model.dto.MemberDto;
import com.kh.spring.util.PageInfo;
import com.kh.spring.util.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j	
@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardMapper boardMapper;
	private final Pagination pagination;
	//private final MyFIleUpload uploadFile;	

	
	/*
	 * 	1. @Transactional은 public 메소드에사만 동작
	 * 
	 * 	2. 같은 클래스 안에서 메서드끼리 호출하면 트랜잭션 안걸림
	 * 
	 * 	3. CheckedException은 기본적으로 롤백 안됨
	 * 
	 * 	4. 의존성 이슈 => 
	 */
	// 안정장치 X -> 성능 최적화 + 의도 표시
	@Transactional(readOnly = true)// readOnly => DB작업을 수행할 때 이 트랜잭션은 데이터 변경을 안함 이라는 힌트를 주는것과 같음 => 조회성능이 향상됨
	public Map<String, Object> findAll(int page) {
		
		// 유효성검증
		
		// 실질적인 비즈니스 로직 => 페이징 처리를 위한 PageInfo객체 생성 및 페이징 처리후 게시글 조회
		
		if(page < 1) {
			throw new InvalidParameterException("잘못된 접근입니다.");
		}
		
		
		int totalCount = boardMapper.selectTotalCount();
		
		//log.info("총 게시글 수 {}", totalCount);
		
		PageInfo pi = pagination.getPageInfo(totalCount, page, 5, 3);
		RowBounds rb = new RowBounds((page-1)*5,5); // 직접 사용하는것은 비권장 전체를조회한뒤 땡겨오는거라서 성능에 안좋음  => 실제로 페이징 처리시에는 SQL문에서 해결(OFFSET문법)하는 것을 권장
		List<BoardDto> boards = boardMapper.findAll(rb);
		//log.info("게시글 목록 : {}", boards);
		
		return Map.of("boards", boards, "pi", pi);
		
		
	}
	// <tx:annotaion-driven이 켜지면 스프링이 @Transactional이 붙은 메서드를 발견해서 포록시로 감쌈
	// 프록시 객체 내부에서 save()메서드를 호출할 때 connection.setAutoCommit(false)로 돌리고 호출
	// 그 후 메소드 종료시 commit()을 호출 예외 발생시 rollback()
	@Transactional 
	public void save(BoardDto board, MultipartFile upfile, HttpSession session) {
		//  유효성 검사
		// 1. 권한 검증
		validateUser(board, session);
		
		// 2. 유효성 검증
		validateContent(board);
		
		// 3. 파일이 있을 경우 이름을 바꿔서 서버에 업로드를 하고 파일 정보를 board의 필드에 대입
		fileUpload(board, upfile, session);
		//uploadFile.upload(board, upfile, session);
		
		// 4. 
		int result = boardMapper.save(board);
		if(result != 1) {
			throw new RuntimeException("관리자에게 문의하세요");
		}
	}
	private void validateUser(BoardDto board, HttpSession session) {
		String boardWriter = board.getBoardWriter();
		MemberDto userInfo = (MemberDto)session.getAttribute("userInfo");
		if(userInfo == null || !userInfo.getUserId().equals(boardWriter)) {
			throw new AuthorizationException("권한없는 요청입니다");	
		}
	}
	
	private void validateContent(BoardDto board) {
		if(board.getBoardTitle().trim().isEmpty()||board.getBoardContent().trim().isEmpty()) {
			throw new InvalidParameterException("유효하지 않은 요청입니다.");
		}
		String boardTitle = board.getBoardTitle().replaceAll("<", "&lt").replaceAll("\n", "<br>");
		String boardContent = board.getBoardContent().replaceAll("<", "&lt").replaceAll("\n", "<br>");
		if(board.getBoardTitle().contains("이승철바보")) {
			boardTitle = board.getBoardTitle().replaceAll("이승철바보", "글쓴사람바보");
		}
		board.setBoardTitle(boardTitle);
		board.setBoardContent(boardContent);
	}
	
	private void fileUpload(BoardDto board, MultipartFile upfile, HttpSession session) {
		
		if(!upfile.getOriginalFilename().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("KH_");
			sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			sb.append("_");
			sb.append((int)(Math.random()*900)+100);
			sb.append(upfile.getOriginalFilename().substring(upfile.getOriginalFilename().lastIndexOf(".")));
		
			ServletContext application = session.getServletContext();
			String savePath = application.getRealPath("/resources/files/");
			
			try {
				upfile.transferTo(new File(savePath + sb.toString()));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			board.setOriginName(upfile.getOriginalFilename());
			board.setChangeName("/spring/resources/files/" + sb.toString());
		
		}
		
	}
	
	public BoardDto findByBoardNo(Long boardNo) {
		if(boardNo < 1 || boardNo == null) {
			throw new InvalidParameterException("유효하지 않은 요청입니다.");
		}
		
		int result = boardMapper.increaseCount(boardNo);
		if(result != 1) {
			throw new InvalidParameterException("관리자에게 문의해주세요");
		}
		BoardDto board = boardMapper.findByBoardNo(boardNo);
		if(board == null) {
			throw new InvalidParameterException("존재하지 않는 게시글입니다.");
		}
		return board;
	}
	
	public Map<String, Object> findByKeyword(String condition, String keyword, int page) {
		// 사용자가 선택한 condition과 사용자가 입력한 keyword를 가지고
		// DB상에서 조건을 걸어 검색해서 나온 조회 결과물을
		// 페이징 처리까지 끝내고 난뒤 페이징객체와 함께 응답할 것
		// 넘어온값이 유효한지 확인하는 것은 건너뜀
		
		int searchedCount = boardMapper.findByKeywordCount(condition, keyword);
		
		//log.info("총 개수는 {}", searchedCount);
		
		PageInfo pi = pagination.getPageInfo(searchedCount, page, 3, 3);
		RowBounds rb = new RowBounds((page-1)*3,3);
		
		List<BoardDto> boards = boardMapper.findByKeyword(condition, keyword, rb);
		//log.info("검색 결과 {}",boards);
		
		return Map.of("boards", boards, "pi", pi);
		
		
		
	}
	public int count() {
		return boardMapper.selectTotalCount();
	}
	
	
}
