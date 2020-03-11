package com.kh.spring.notice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.common.Pagination;
import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.member.model.vo.Member;
import com.kh.spring.notice.model.service.NoticeService;
import com.kh.spring.notice.model.vo.Notice;

@Controller
@RequestMapping("/notice/*")
@SessionAttributes({ "loginMember", "msg" })
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	/*
	 * ModelAndView 객체 - Model : 응답 페이지에 값(data)를 전달할 때 Map 형식으로 저장하여 전달하는 객체
	 * 
	 * - View : requestDispatcher를 이용한 페이지 이동 시 이동할 페이지의 정보(url)를 담는 객체
	 *
	 * - ModelAndView : 컨트롤러 응답 처리 후 응답할 View와 View에 전달할 값을 저장하는 객체
	 * 
	 */

	// 공지사항 목록 출력
	@RequestMapping("list")
	public ModelAndView noticeList(ModelAndView mv,
			@RequestParam(value = "searchKey", required = false) String searchKey,
			@RequestParam(value = "searchValue", required = false) String searchValue,
			@RequestParam(value = "currentPage", required = false) Integer currentPage) {
		// currentPage
		// searchKey
		// searchValue

		try {
			// 검색 조건이 있는지 확인하여 map에 세팅
			Map<String, String> map = null;
			if (searchKey != null && searchValue != null) {
				map = new HashMap<String, String>();
				map.put("searchKey", searchKey);
				map.put("searchValue", searchValue);
			}
			// 1. 전체 공지사항 게시글 수 조회(페이징 처리를 위해서)
			int listCount = noticeService.getListCount(map);

			// 현재 페이지 계산
			if (currentPage == null)
				currentPage = 1;
			// 페이지 정보 저장
			PageInfo pInf = Pagination.getPageInfo(10, 10, currentPage, listCount);

			// 2. 공지사항 목록 조회
			List<Notice> list = noticeService.selectList(map, pInf);
			mv.addObject("list", list);
			mv.addObject("pInf", pInf);
			mv.setViewName("notice/noticeList");

		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("errorsMsg", "공지사항 목록 조회중 오류 발생");
			mv.setViewName("common/errorPage");
		}
		return mv;

	}
	
	// 공지사항 상세조회
	@RequestMapping("detail")
	public String noticeDetail(Model model, int no) {
		
		try {
			Notice notice = noticeService.noticeDetail(no);
			String msg ="";
			if(notice !=null) {
				msg = "글조회 성공";
			}else {
				msg = "글조회 실패";
			}
			model.addAttribute("notice", notice);
			return "notice/noticeDetail";
			
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "공지사항 상세조회 에러나쪄염");
			return "common/errorPage";
		}
	}
	// 공지사항 글쓰기
	@RequestMapping("insertForm")
	public String insertForm() {
		return "notice/noticeInsert";
	}
	
	@RequestMapping("insert")
	public String noticeInsert(Model model,Notice notice, String title, String content) {
		
		Member loginMember = (Member)model.getAttribute("loginMember");
		String memberWriter = loginMember.getMemberId();
		
		notice.setNoticeTitle(title);
		notice.setNoticeContent(content);
		notice.setNoticeWriter(memberWriter);
		
		try {
			
			String msg ="";
			int result = noticeService.noticeInsert(notice);		
			if(result>0) msg = "글쓰기 성공";
			else 		 msg = "글쓰기 실패";
			return "redirect:list";
			
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "공지사항 글쓰기 에러나쪄염");
			return "common/errorPage";
		}
	}
	
	@RequestMapping("delete")
	public String noticeDelete(int no,Model model) {
		
		try {
			
			String msg ="";
			int result = noticeService.noticeDelete(no);
			if(result>0) msg = "글삭제 성공";
			else		msg="글삭제 실패";
			
			return "redirect:list";
			
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "공지사항 삭제 에러나쪄염");
			return "common/errorPage";
		}
	}
	@RequestMapping("updateForm")
	public String updateForm(int no,Model model) {
		
		try {
			
		String msg = ""; 
		Notice notice = noticeService.noticeUpdateForm(no);
		System.out.println(notice);
		if(notice != null) msg = "불러오기 성공";
		else 			msg = "불러오기 실패";
		model.addAttribute("notice", notice);
		return "notice/noticeUpdate";
		
		
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "공지사항  수정화면가져오기 에러나쪄염");
			return "common/errorPage";
		}
		
	}
	@RequestMapping("update")
	public String noticeUpdate(Model model, int no, String title, String content, Notice notice, RedirectAttributes rdAttr) {
		
		try {
			
			String msg ="";
			notice.setNoticeNo(no);
			notice.setNoticeTitle(title);
			notice.setNoticeContent(content);
			System.out.println(notice);
			
			int result = noticeService.noticeUpdate(notice);
			if(result>0) msg = "수정 성공";
			else msg = "수정실패";
			rdAttr.addFlashAttribute("msg", msg);
			// Model 쓰는 경우 msg 알림창 띄울때는 얘를 쓴다아 고말이야  ㅇㅋ!
			return "redirect:detail?no="+no;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "공지사항  수정화면가져오기 에러나쪄염");
			return "common/errorPage";
		}
	}
	
}
