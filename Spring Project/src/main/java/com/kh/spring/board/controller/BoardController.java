package com.kh.spring.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.FileRename;
import com.kh.spring.common.Pagination;
import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.member.model.vo.Member;

@SessionAttributes({ "loginMember", "msg", "detailUrl" })
@Controller
@RequestMapping("/board/*")
public class BoardController {

	@Autowired
	private BoardService boardService;

	// 게시글 목록 조회(+검색결과)
	@RequestMapping("list")
	public String boardList(Model model, @RequestParam(value = "currentPage", required = false) Integer currentPage,
			@RequestParam(value = "searchKey", required = false) String searchKey,
			@RequestParam(value = "searchValue", required = false) String searchValue,
			@RequestParam(value = "searchCategory", required = false) String[] searchCategory) {
		try {
			// 검색 조건이 있는지 확인하여 map에 세팅
			Map<String, Object> map = null;
			if (searchKey != null && searchValue != null) {
				map = new HashMap<String, Object>();
				map.put("searchKey", searchKey);
				map.put("searchValue", searchValue);
				map.put("searchCategory", searchCategory);
			}

			// 전체 게시글 수 조회
			int listCount = boardService.getListCount(map);

			// 현재페이지
			if (currentPage == null)
				currentPage = 1;

			// 페이지 정보 저장
			PageInfo pInf = Pagination.getPageInfo(5, 10, currentPage, listCount);

			// 게시글 목록 조회
			List<Board> list = boardService.selectList(map, pInf);

			// 썸네일 목록 조회
			List<Attachment> thList = boardService.selectThumbnailList(list);

			model.addAttribute("list", list);
			model.addAttribute("pInf", pInf);
			model.addAttribute("thList", thList);

			return "board/boardList";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "게시글 목록 조회 과정에서 오류 발생");
			return "common/errorPage";
		}

	}

	// 게시글 등록 화면 이동
	@RequestMapping("insertForm")
	public String insertForm() {
		return "board/boardInsert";
	}

	// 게시글 등록
	@RequestMapping("insert")
	public String insert(Board board, // 커맨드 객체(boardVo랑 jsp에서 보내는 name의 값이랑 일치할 경우 바로 사용 가능)
			Model model, // session 접근용
			HttpServletRequest request, // 파일 경로
			RedirectAttributes rdAttr, // 리다이렉트시 메세지 전달용
			@RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
			@RequestParam(value = "images", required = false) List<MultipartFile> images) {

		// Session에서 회원 번호 얻어오기
		Member loginMember = (Member) model.getAttribute("loginMember");
		int boardWriter = loginMember.getMemberNo();

		// 회원 번호를 커맨드 객체 board에 저장(커맨드 객체 재활용)
		board.setBoardWriter(boardWriter + "");

		// 파일 저장 경로
		String root = request.getSession().getServletContext().getRealPath("resources");
		String savePath = root + "/" + "/uploadFiles";

		// 저장 폴더 선택
		File folder = new File(savePath);

		// 만약 해당 폴더가 없는 경우 -> 폴더 만들기
		if (!folder.exists())
			folder.mkdir();

		try {

			// 전송된 파일 확인
			// @RequestParam을 이용하여 input type="file" 요소를 얻어올 경우
			// 값이 없을 때는 value "" (빈문자열)이 전달됨.

			// 썸네일 확인
			System.out.println("thumbnail : " + thumbnail.getOriginalFilename());

			// 이미지 업로드 확인
			for (int i = 0; i < images.size(); i++) {
				System.out.println("image[" + i + "] : " + images.get(i).getOriginalFilename());
			}

			// 업로드된 파일을 하나의 List에 저장
			List<Attachment> files = new ArrayList<Attachment>();
			Attachment at = null;

			// thumbnail 업로드 이미지 rename 작업 후 추가
			if (!thumbnail.getOriginalFilename().equals("")) {
				// thumbnail이 등록된 경우

				// 파일명 rename
				String changeFileName = FileRename.rename(thumbnail.getOriginalFilename());

				// Attachment 객체 생성
				at = new Attachment(thumbnail.getOriginalFilename(), changeFileName, savePath);

				// thumbnail의 fileLevel == 0
				at.setFileLevel(0);

				// files List에 추가
				files.add(at);
			}

			// images 업로드 이미지 rename 작업

			for (MultipartFile mf : images) {
				if (!mf.getOriginalFilename().equals("")) {
					// image가 등록된 경우

					// 파일명 Rename

					String changeFileName = FileRename.rename(mf.getOriginalFilename());

					// Attachment 객체 생성
					at = new Attachment(mf.getOriginalFilename(), changeFileName, savePath);

					// images의 fileLevel == 1
					at.setFileLevel(1);

					// files List에 추가
					files.add(at);

				}
			}

			// 게시글 + 이미지 사진 Service 호출
			int result = boardService.insertBoard(board, files);

			String msg = null;
			String url = null;

			if (result > 0) { // DB에 게시글 삽입 성공 시
				// 서버에 파일 저장
				for (Attachment file : files) {
					if (file.getFileLevel() == 0) { // 현재 접근한 파일이 썸네일이면
						thumbnail.transferTo(new File(file.getFilePath() + "/" + file.getFileChangeName()));
						// transferTo() 가 정상 호출 될 경우 파일이 저장됨.
					} else { // 현재 접근한 파일이 일반 이미지면
						for (MultipartFile mf : images) {
							if (mf.getOriginalFilename().equals(file.getFileOriginName())) {
								mf.transferTo(new File(file.getFilePath() + "/" + file.getFileChangeName()));
								break;
							}
						}
					}

				}

				msg = "게시글 등록 성공";
				// url = "detail?no="+result+"&currentPage=1";
				// 상세조회 완성 후 사용

				url = "list";
			} else {
				msg = "게시글 등록 실패";
				url = "list";
			}

			rdAttr.addFlashAttribute("msg", msg);
			return "redirect:" + url;

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "게시글 등록 과정에서 오류 발생");
			return "common/errorPage";
		}
	}

	// 게시글 상세보기
	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public String boardDetail(Integer no, // 조회할 글번호
			Model model, // 요청 응답에 사용
			RedirectAttributes rdAttr, // 조회 실패시 이전목록으로 리다이렉트 할때 메세지 전달
			HttpServletRequest request) { // 이전 페이지(referer) url 얻어오기

		String beforeUrl = request.getHeader("referer");

		try {

			// 1) 글번호를 이용하여 Board 테이블에서 해당 게시글 조회
			Board board = boardService.selectBoard(no);
			System.out.println(board);
			// 2) 게시글 조회 성공 시 글 번호를 이용하여 해당 게시글에 포함된 이미지 목록 조회
			if (board != null) {
				List<Attachment> files = boardService.selectFiles(no);
				if (!files.isEmpty()) {
					model.addAttribute("files", files);
				}

				// 3) 조회수 증가
				int result = boardService.increaseCount(no);

				board.setBoardCount(board.getBoardCount() + 1);

				model.addAttribute("board", board);

				return "board/boardDetail";

			} else {
				rdAttr.addFlashAttribute("msg", "게시글 상세조회 실패");
				return "redirect:" + beforeUrl;
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "게시글 상세조회 과정에서 오류 발생");
			return "common/errorPage";
		}

	}

	// 게시글 수정 화면 이동

	@RequestMapping("updateForm")
	public String updateForm(Integer no, // 수정화면에 보여질 게시글 조회용 글 번호
			Model model, // 게시글 정보+이미지파일 정보를 View에 전달
			HttpServletRequest request) { // 수정 완료 후
											// 상세보기 페이지 주소(이젠페이지 주소) 저장
		// 이전 페이지 (상세조회 페이지) 주소를 detailUrl에 저장
		String detailUrl = request.getHeader("referer");

		// @SessionAttributes에 "detailUrl" 추가 후
		// Session에 detailUrl 올리기
		model.addAttribute("detailUrl", detailUrl);

		try {

			// 1 ) 게시글 상세 조회
			Board board = boardService.selectBoard(no);
			// 2) 게시글 상세조회 성공시 파일 목록 조회

			if (board != null) {
				List<Attachment> files = boardService.selectFiles(no);
				if (!files.isEmpty()) { // 해당 게시글에 이미지가 있다면
					model.addAttribute("files", files);
				}
			}

			// 3) 조회된 정보를 View에 전달 후 Foaward
			board.setBoardContent(board.getBoardContent().replace("<br>", "\r\n"));
			model.addAttribute("board", board);
			return "board/boardUpdate";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "게시글 수정화면 전환 과정에서 오류 발생");
			return "common/errorPage";
		}

	}

	// 게시글 수정
	@RequestMapping("update")
	public String updateBoard(Integer no, // 수정할 글 번호
			Board board, // View에서 전달된 게시글 수정 내용(커맨드 객체)
			Model model, // errorMsg 전달용도
			RedirectAttributes rdAttr, // 수정 성공, 실패 메세지 전달용도
			HttpServletRequest request, // 수정 또는 새로 추가된 파일 저장을 위한 파일 경로 추출 용도
			@RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
			// required=false : 요청된 파라미터 중에서 뒤에 작성될 매개변수 값은 필수가 아니다.
			@RequestParam(value = "images", required = false) List<MultipartFile> images) {
			// = request.getParameter("images");
		
		// 1) Session scope에서 detailUrl 얻어오기
		// -> 수정을 성공하든 실패하든 상세조회 다시 리다이렉트 하기 위함.
		String detailUrl = (String)model.getAttribute("detailUrl");

		// 2) no (글번호) board (커맨드객체)
		board.setBoardNo(no);

		// 3) 수정 또는 새롭게 추가된 파일이 저장될 경로 얻어오기
		String root = request.getSession().getServletContext().getRealPath("resources");
		String savePath = root + "/uploadFiles";

		// 저장폴더가 있는지 검사하여 없을 경우 생성하기
		File folder = new File(savePath);
		if (!folder.exists())
			folder.mkdir();

		try {

			// 4)게시글 (+게시글 이미지) 수정용 서비스 호출
			int result = boardService.updateBoard(board, thumbnail, images, savePath);
			// 5) 서비스 호출 결과에 따른 메세지 처리
			String msg = null;
			if(result>0) msg = "게시글 수정 성공";
			else		msg = "게시글 수정 실패";
			rdAttr.addFlashAttribute("msg",msg);
			
			// 수정 후 상세 조회 화면 요청
			return "redirect:" + detailUrl;
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "게시글 수정 과정에서 오류 발생");
			return "common/errorPage";
		}
	}

}
