package com.kh.spring.board.model.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDAO;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.FileRename;
import com.kh.spring.common.vo.PageInfo;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDAO boardDAO;

	/**
	 * 전체 게시글 수 조회용 Service
	 * 
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	@Override
	public int getListCount(Map<String, Object> map) throws Exception {
		return boardDAO.getListCount(map);
	}

	/**
	 * 게시글 목록 조회용 Service
	 * 
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	@Override
	public List<Board> selectList(Map<String, Object> map, PageInfo pInf) throws Exception {
		return boardDAO.selectList(map, pInf);
	}

	/**
	 * 게시글 등록용 Service
	 * 
	 * @param board
	 * @param files
	 * @return result
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertBoard(Board board, List<Attachment> files) throws Exception {

		int result = 0;

		// 1) 다음 SEQ_BNO 얻어오기
		int boardNo = boardDAO.selectNextNo();
		// 2) 게시글(board) DB 삽입
		if (boardNo > 0) {
			// DB에 Content 저장 시 개행 문자를 <br>로 변경
			board.setBoardContent(board.getBoardContent().replace("\r\n", "<br>"));

			// 조회한 boardNo를 board에 set
			board.setBoardNo(boardNo);
			// title, content, writer, boardNo

			// 게시글 DB 등록
			result = boardDAO.insertBoard(board);

		}
		// 3) 이미지(attachment) DB 삽입

		if (result > 0 && !files.isEmpty()) {
			result = 0; // result 재활용

			// files의 데이터를 하나씩 반복접근하여 DB에 삽입
			for (Attachment at : files) {
				at.setBoardId(boardNo); // 게시글 번호 set
				result = boardDAO.insertAttachment(at);

				if (result == 0) {
					throw new Exception(); // 예외를 던져줌
				}

			}

		}

		if (result > 0) {
			// result에 글번호 저장
			result = boardNo;
		}
		return result;
	}

	
	/**
	 * 썸네일 목록 조회용 Service
	 * 
	 * @param map
	 * @param pInf
	 * @return thList
	 * @throws Exception
	 */
	@Override
	public List<Attachment> selectThumbnailList(List<Board> list) throws Exception {
		return boardDAO.selectThumbnailList(list);
	}
	
	/** 게시글 상세조회용 Service
	 * @param no
	 * @return board
	 * @throws Exception
	 */
	@Override
	public Board selectBoard(Integer no) throws Exception {
		return boardDAO.selectBoard(no);
	}

	@Override
	public List<Attachment> selectFiles(Integer no) throws Exception {
		return boardDAO.selectFiles(no);
	}

	/** 조회수 증가용 Service
	 * @param no
	 * @return result
	 * @throws Exception
	 */
	@Override
	public int increaseCount(Integer no) throws Exception {
		return boardDAO.increaseCount(no);
	}

	/** 게시글 수정용 Service
	 * @param board
	 * @param thumbnail
	 * @param images
	 * @param savePath
	 * @return result
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	@Override
	public int updateBoard(Board board, MultipartFile thumbnail, List<MultipartFile> images, String savePath)
			throws Exception {
		
		
		
		
		
		// 기존 게시글 이미지 정보를 DB에서 조회
		List<Attachment> files = boardDAO.selectFiles(board.getBoardNo());
		// 원래글 사진들 = 썸네일 + 일반 이미지
		
		Attachment beforeTh = null;
		
		List<Attachment> beforeImages = new ArrayList<Attachment>();
		
		for(Attachment at : files) {
			if(at.getFileLevel() == 0) { 
				beforeTh = at;
			}else {
				beforeImages.add(at);
			}
		}
		
		
		
		
		
		
		// 새롭게 삽입할 파일 목록
		List<Attachment> insertList = new ArrayList<Attachment>();
		
		// 기존 행을 수정할 파일 목록 
		List<Attachment> updateList = new ArrayList<Attachment>();
		
		Attachment file = null; // 리스트에 추가될 Attachment 객체를 참조할 변수 선언
		
		
		
		
		
		
		// 썸네일 비교
		// 새롭게 등록된 썸네일이 있는지 확인
		if(!thumbnail.getOriginalFilename().equals("")) { // 썸네일 신규 O
			// rename 작업 진행
			String changeFileName = FileRename.rename(thumbnail.getOriginalFilename());
			
			
			
			
	
			// 썸네일이 기존에 있을때 --> update 진행
			if(beforeTh != null) {
				file = new Attachment(beforeTh.getFileNo(), 
						thumbnail.getOriginalFilename(),
						changeFileName);
				
				file.setFileLevel(0); // 썸네일 파일레벨은 0
				updateList.add(file);
			}
			
			
			
			
			
			// 썸네일 기존에 없을때  --> insert 진행
			else {
				file = new Attachment(thumbnail.getOriginalFilename(),
									changeFileName, 
									savePath);
				file.setFileLevel(0); //썸네일 파일레벨은 0
				file.setBoardId(board.getBoardNo());
				// -> 신규 파일은 BoardId가 없으므로 추가해줌
				insertList.add(file);
			}
			
		}
		
		
		
		
		
		
		
		// 일반 이미지 비교 
		int filesIndex = 0; 
		// 기존 파일 목록 (beforeImages)에 얻어올 인덱스 값을 저장할 변수 선언
		
		for(int i=0; i<images.size(); i++) {
			// 일반 이미지 신규가 있을때 
			if(!images.get(i).getOriginalFilename().equals("")) {
				// rename 처리
				String changeFileName
					= FileRename.rename(images.get(i).getOriginalFilename());
				
				// 일반 이미지 기존에 있을때 --> update
				if(beforeImages.size() > 0 && i<beforeImages.size()) {
					file = new Attachment(beforeImages.get(filesIndex).getFileNo()
							, images.get(i).getOriginalFilename(),
							changeFileName);
					
					file.setFileLevel(1);
					updateList.add(file);
					filesIndex++;
					
					
					
					
					
					
				// 일반 이미지 기존에 없을때 --> insert
				}else {
					file = new Attachment(images.get(i).getOriginalFilename(),
							changeFileName,
							savePath);
					file.setFileLevel(1);
					file.setBoardId(board.getBoardNo());
					// 신규 파일은 boardId가 없으므로 추가
					insertList.add(file);
				}
			
			}
			
		}
		
		
		
		
		
		int result = 0;
		
		// 1) 게시글 수정
		// 개행문자 변환(\r\n --> <br>)
		board.setBoardContent(board.getBoardContent().replace("\r\n", "<br>"));
		
		result = boardDAO.updateBoard(board);
		
		// 2) 게시글 수정 성공 & 새로 삽입할 파일(insertList)가 있을 경우
		if(result > 0 && !insertList.isEmpty()) { 
			// List, Map, Collection 등 공간개념일때 비어있느냐를 따질때는 isEmpty() 를 쓴다
			
			result = 0; // result 재활용
			
			// 향상된 for문을 이용하여 DAO를 반복호출함(정상 처리되면 for문이 계속 돈다)
			for(Attachment at : insertList) {
				result = boardDAO.insertAttachment(at);
				
				if(result == 0) { // 파일 삽입에 실패한 경우
					throw new Exception("파일 삽입 과정에서 오류 발생"); // 콘솔에 이 메세지가 뜬다
					// throw 하면 롤백과 동시에 예외처리가 된다.
				}
			}
		}
		
		// 3) 기존 Attachment 테이블 수정할 파일 정보(updateList)가 있을 경우
		if(result > 0 && !updateList.isEmpty()) {
			result = 0; // result 재활용
			
			for(Attachment at : updateList) {
				result = boardDAO.updateAttachment(at);
				
				if(result == 0){
					throw new Exception("파일 수정 과정에서 오류 발생");
				}
			}
		}
		
		// 4) DB 수정이 정상적으로 처리된 경우 파일 저장
		if(result > 0) { // 정상적으로 처리가 되었다
			// insertList, updateList 합치기
			insertList.addAll(updateList);
			
			for(Attachment at : insertList) {
				if(at.getFileLevel() == 0) { // 현재 접근한 파일이 썸네일이면.
					thumbnail.transferTo(new File(savePath + "/" + at.getFileChangeName()));
				}else { // 현재 접근한 파일이 일반이미지면.
					for(MultipartFile mf : images) {
						
						if(mf.getOriginalFilename().equals(at.getFileOriginName())) {
							// 진짜 파일						// 파일의 정보일뿐.
							// 진짜 파일의 이름과, 파일의 정보를 가지고있는 이름이 같다면 
							mf.transferTo(new File(savePath + "/" + at.getFileChangeName()));
							break;
						}
					}
				}
			}
		}
		
		// 수정 전 이미지를 서버에서 삭제
		for(Attachment at : files) { // 수정 전 이미지 목록 반복 접근
			for(Attachment newImage : insertList) { // 수정 후 이미지 목록 반복 접근
				
				// 수정 전 fileNo와 수정 후 fileNo가 같은 객체가 존재하는 경우
				// --> 수정 전 파일에서 ChangeFileName을 얻어와 서버에서 삭제
				
				if(at.getFileNo() == newImage.getFileNo()) {
					// 이미지 삭제
					File deleteFile = new File(savePath + "/" + at.getFileChangeName());
					deleteFile.delete();
				}
			}
		}
		
		return result;
	}

}
