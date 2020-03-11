package com.kh.spring.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

//프레젠테이션 레이어, 웹 애플리케이션에서 View
// 응답을 처리하는 클래스임을 나타냄 + bean 등록
@Controller // @Component
@RequestMapping("/member/*") // 내부 메소드 레벨에서 매핑되는 주소에 공통되는 부분 작성
@SessionAttributes({ "loginMember", "msg" })
// Model에 담긴 데이터 중 key값이 @SessionAttributes의 매개변수에 작성된 값과 같은경우
// 해당데이터의 scope를 session을 변경
public class MemberController {

	// @Autowired 사용시 bean scanning을 통해 등록된 bean 중
	// 알맞은 bean을 의존성 주입(DI) 해줌
	@Autowired
	private MemberService memberService;

	// 1. HttpServletRequest를 이용하여 파라미터 받기 (기존 sevlet방식)
	/*
	 * @RequestMapping(value="login", method=RequestMethod.POST) // /member/login
	 * 요청을 매핑하는 메소드 + post 방식 요청만 // --> 이주소로 들어오는 post 방식의 요청만 매핑하겠다 public String
	 * memberLogin(HttpServletRequest request) { // 매개변수로 HttpServletRequest를 작성하면
	 * // 스프링 컨테이너(스프링 프레임워크)가 자동으로 요청페이지의 // HttpServletRequest 객체를 매개변수로 주입해줌.
	 * String memberId = request.getParameter("memberId"); String memberPwd =
	 * request.getParameter("memberPwd");
	 * 
	 * System.out.println(memberId + "/" + memberPwd);
	 * 
	 * return "main"; }
	 */

	// 2. @RequestParam 어노테이션
	/*
	 * request 객체를 이용하여 파라미터를 전달받는 어노테이션
	 * 
	 * @RequestParam("name속성값") String 원하는 변수명 요청페이지의 input값(value)이 비어있다면 ""(빈문자열)로
	 * 전달됨.
	 * 
	 * 
	 */
	/*
	 * @RequestMapping(value="login", method=RequestMethod.POST) public String
	 * memberLogin(@RequestParam("memberId") String memberId,
	 * 
	 * @RequestParam("memberPwd") String memberPwd ) {
	 * 
	 * System.out.println(memberId + "/" + memberPwd);
	 * 
	 * return "main"; }
	 */

	// 3. @RequestParam 어노테이션 생략
	/*
	 * @RequestParam 어노테이션 생략 시 매개변수명을 전달되는 파라미터의 name 속성 값과 똑같이 작성하면 해당 매개변수에 파라미터가
	 * 매핑되어 자동 주입됨.
	 * 
	 * 어노테이션 생략은 코드를 읽는 가독성을 낮추므로 어노테이션을 작성하는것을 권장함.
	 * 
	 * @ModelAttribute와 @RequestParam혼용시 둘중 하나만 생략하고 나머지 하나는 명시하는 형태로 많이 사용됨.
	 */

	/*
	 * @RequestMapping(value="login",method=RequestMethod.POST) public String
	 * memberLogin(String memberId, String memberPwd) { System.out.println(memberId
	 * +"/"+memberPwd);
	 * 
	 * return "main"; }
	 */

	// 4. @ModelAttribute를 이용한 파라미터 전달받기
	/*
	 * 요청 페이지에서 전달하는 파라미터가 많고 전달되는 파라미터들이 특정 VO 클래스의 필드에 저장될 형태일때 사용.
	 * 
	 * 회원가입 -> Member 객체
	 * 
	 * (주의사항) 1) 전달되는 파라미터의 name 속성값과 VO 클래스의 필드명이 같아야한다. 2) VO클래스에는 기본 생성자 +
	 * setter가 반드시 존재해야함.
	 * 
	 * --> 커맨드 객체
	 */

	/*
	 * @RequestMapping(value="login",method=RequestMethod.POST) public String
	 * memberLogin(@ModelAttribute Member member) {
	 * 
	 * System.out.println(member.getMemberId() + "/" +member.getMemberPwd());
	 * 
	 * return "main";
	 * 
	 * }
	 */

	// 5. @ModelAttribute 생략
	/*
	 * @RequestMapping(value="login",method=RequestMethod.POST) public String
	 * memberLogin(Member member, HttpSession session, Model model) { // Model은 응답으로
	 * 전달하고자하는 데이터를 맵 형식(K,V)으로 담아 전달하는 역할. // scope는 request임(기본적으로). // Model은 상황에
	 * 따라서 scope가 달라질수도 있다.
	 * 
	 * 
	 * System.out.println(member.getMemberId() + "/" + member.getMemberPwd());
	 * 
	 * try { Member loginMember = memberService.loginMember(member);
	 * System.out.println("loginMember: "+ loginMember);
	 * 
	 * if(loginMember != null) { session.setAttribute("loginMember", loginMember);
	 * }else { session.setAttribute("msg", "로그인 정보가 유효하지 않습니다."); }
	 * 
	 * //return "main"; // forward 방식 return "redirect:/main"; // redirect 방식
	 * 
	 * }catch(Exception e) { e.printStackTrace();
	 * model.addAttribute("errorMsg","로그인 과정에서 오류 발생");
	 * 
	 * return "common/errorPage"; // 만약에 예외가 발생을 했으면 }
	 * 
	 * }
	 */

	// @SessionAttribute 사용하기
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public String memberLogin(Member member, Model model) {
		// Model은 응답으로 전달하고자하는 데이터를 맵 형식(K,V)으로 담아 전달하는 역할.
		// scope는 request임(기본적으로).
		// Model은 상황에 따라서 scope가 달라질수도 있다.

		System.out.println(member.getMemberId() + "/" + member.getMemberPwd());

		try {
			Member loginMember = memberService.loginMember(member);
			System.out.println("loginMember: " + loginMember);

			if (loginMember != null) {
				model.addAttribute("loginMember", loginMember);
			} else {
				model.addAttribute("msg", "로그인 정보가 유효하지 않습니다.");
			}

			// return "main"; // forward 방식
			return "redirect:/main"; // redirect 방식

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "로그인 과정에서 오류 발생");

			return "common/errorPage";
			// 만약에 예외가 발생을 했으면
		}

	}

	// 로그아웃
	// value 만 쓴형태
	@RequestMapping("logout") // method를 지정하지 않으면 전송방식에 상관없이 매핑된다.
	public String memberLogout(SessionStatus status/* HttpSession session */) {
		// 세션 무효화
		// session.invalidate();

		// SessionStatus 객체 : 세션의 상태를 관리할 수 있는 객체
		// @SessionAttributes 사용 시
		// Session을 무효화 시키기 위해서는
		// SessionStatus 객체를 사용해야 한다.
		status.setComplete();

		return "redirect:/main";
	}

	// 회원가입 페이지 이동
	@RequestMapping("signUpForm")
	public String signUpForm() {
		return "member/signUpForm";
	}

	// 회원가입
	@RequestMapping("signUp")
	public String signUp(Member member, Model model, String phone1, String phone2, String phone3, String post,
			String address1, String address2,
			@RequestParam(value = "memberInterest", required = false) String[] interest) {
		// @RequestParam 의 required : 해당 파라미터가 필수인지 여부를 지정
		// 기본값은 true

		// 전화번호를 '-' 를 구분자로 하여 하나의 String 으로 합치기
		String memberPhone = phone1 + "-" + phone2 + "-" + phone3;

		// 주소를 ',' 를 구분자로 하여 합침
		String memberAddress = post + "," + address1 + "," + address2;

		// 관심분야를 ','를 구분자로 하여 합침
		String memberInterest = null;
		if (interest != null) {
			memberInterest = String.join(",", interest);
		}
		Member signUpMember = new Member(member.getMemberId(), member.getMemberPwd(), member.getMemberName(),
				memberPhone, member.getMemberEmail(), memberAddress, memberInterest);

		try {
			int result = memberService.signUp(signUpMember);
			String msg = null;
			if (result > 0)
				msg = "가입성공";
			else
				msg = "가입실패";

			model.addAttribute("msg", msg);
			return "redirect:/main";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "회원가입과정에서 오류발생");
			return "common/errorPage";
		}

		/*
		 * 1. 비밀번호를 평문으로 저장하면 어떻게 될까? --> 범죄행위
		 * 
		 * 2. SHA-512 해시함수를 이용한 암호화 -> 단방향 해시함수(복호화 불가능)== 암호의 해독이 안됌 문제점 : 같은 비밀번호는 암호화
		 * 내용(다이제스트)이 똑같다. ex) 1234 -> abcd 1234 -> abcd -> 다이제스트가 많이 모이면 원래 비밀번호를 검색을
		 * 통해 찾아낼 가능성이 있음. (해킹에 취약)
		 * 
		 * - 일반적인 장비로도 1초에 56억개의 다이제스트를 만들 수 있음
		 * 
		 * 3. bcrypt 해시함수를 이용한 암호화(salting 기법) - 입력된 문자열을 암호화 할 때 바로 해시함수에 대입하는 것이 아닌
		 * 임의의 값(salt)을 문자열에 추가하여 암호화를 진행
		 * 
		 * Spring Security 모듈에서 지원해줌 -> pom.xml 라이브러리 추가
		 * 
		 */

	}

	/*
	 * @ResponseBody란?
	 * 
	 * 메소드에서 리턴되는 값을 View를 통해 출력하는 것이 아닌 리턴값을 HTTP에 Response Body에 담는 역할을 함 --> jsp로
	 * 화면이 이동되는것이 아닌 기존페이지로 데이터만 전달됨
	 * 
	 */

	// 리턴종류
	/*
	 * String ==> view 이동 modelandview ==> view 이동 ajax data ==> data 이동 json ==>
	 * data 이동 XML ==> data 이동
	 */

	// 아이디 중복 검사
	@ResponseBody
	@RequestMapping("idDupCheck")
	public String indDupCheck(String memberId, Model model) {
		try {
			return memberService.idDupCheck(memberId) == 0 ? true + "" : false + "";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "아이디 중복체크 과정에서 오류 발생");
			return "common/errorPage";
		}
	}

	// 회원정보(마이페이지) 조회
	@RequestMapping("mypage")
	public String myPage(Model model) {
		// Session Scope에 있는 loginMember를 얻어옴.
		Member loginMember = (Member) model.getAttribute("loginMember");
		// Object 타입으로 반환이됨 -> Member로 강제형변환해야함
		// @SessionAttributes의 매개변수로 작성된 key값을
		// model.getAttribute("key값")을 이용하여
		// Session scope에서 속성값을 얻어 올 수 있음.

		try {
			Member member = memberService.selectMember(loginMember.getMemberNo());
			model.addAttribute("member", member);
			return "member/mypage";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "회원 정보 조회 과정에서 오류 발생");
			return "common/errorPage";

		}
	}

	// 커맨드 객체 사용 조건
	// 1) 기본 생성자
	// 2) setter

	/*
	 * RedirectAttributes - 리타이렉트 시 데이터를 전달 할 수 있는 객체
	 * 
	 * addFlashAttribute() - 리타이렉트로 데이터 전달 시 쿼리스트링으로 전달되지 않게 현재 request에 세팅된
	 * attribute를 잠시 Session scope로 올렸다가 페이지 이동 후 새로 생성된 request에 다시 추가해줌
	 *
	 */

	// 회원 정보 수정
	@RequestMapping("updateMember")
	public String updateMember(Member member, Model model, RedirectAttributes rdAttr, String phone1, String phone2,
			String phone3, String post, String address1, String address2,
			@RequestParam(value = "memberInterest", required = false) String[] interest) {

		Member loginMember = (Member) model.getAttribute("loginMember");
		String memberPhone = phone1 + "-" + phone2 + "-" + phone2;

		String memberEmail = member.getMemberEmail();
		String memberAddress = post + "," + address1 + "," + address2;

		String memberInterest = null;
		if (interest != null) {
			memberInterest = String.join(",", interest);
		}

		Member updateMember = new Member(loginMember.getMemberId(), loginMember.getMemberPwd(),
				loginMember.getMemberName(), memberPhone, memberEmail, memberAddress, memberInterest);

		System.out.println("업뎃멤버 : " + updateMember);

		try {

			int result = memberService.updateMember(updateMember);

			String msg = null;

			if (result > 0)
				msg = "회원 정보가 수정되었습니다.";
			else
				msg = "회원 정보 수정에 실패하였습니다.";

			// model.addAttribute("msg", msg);
			rdAttr.addFlashAttribute("msg", msg);
			model.addAttribute("member", updateMember);
			return "redirect:mypage";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "회원 정보 수정 과정에서 오류 발생");
			return "common/errorPage";
		}
	}

	// 비밀번호 변경 페이지 이동
	@RequestMapping("changePwd")
	public String changePwd() {
		return "member/changePwd";

	}

	@RequestMapping("updatePwd")
	public String updatePwd(Member member, String newPwd1, Model model, RedirectAttributes rdAttr) {
		// 현재 비밀번호에 입력한 값
		// 새로운 비밀번호에 입력한 값
		// 회원번호 또는 회원아이디

		// Session에서 회원 번호를 얻어오기
		int memberNo = ((Member) model.getAttribute("loginMember")).getMemberNo();

		// 커맨드 객체 member를 재활용하여
		// 회원번호와, 현재 비밀번호에 입력한 값을 한번에 저장
		member.setMemberNo(memberNo);

		try {
			int result = memberService.updatePwd(member, newPwd1);
			String msg = null;
			if (result > 0)
				msg = "비밀번호 수정 성공";
			else if (result == 0)
				msg = "비밀번호 변경 실패";
			else
				msg = "현재 비밀번호가 일치 하지 않습니다.";

			rdAttr.addFlashAttribute("msg", msg);

			return "redirect:mypage";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "비밀번호 수정 과정에서 오류발생");
			return "common/errorPage";
		}
	}

	// 회원 탈퇴 페이지 이동
	@RequestMapping("secession")
	public String secession(Model model, Member member, RedirectAttributes rdAttr) {
		Member loginMember = (Member) model.getAttribute("loginMember");

		String memberPwd = member.getMemberPwd();
		loginMember.setMemberPwd(memberPwd);

		try {
			int result = memberService.deleteMember(loginMember);

			String msg = null;

			if (result > 0) {
				msg = "탈퇴성공";
				model.addAttribute("member", loginMember);
			} else if (result == 0) {
				msg = "탈퇴실패";
			} else {
				msg = "비밀번호 불일치";
			}

			rdAttr.addFlashAttribute("msg", msg);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "비밀번호 오류 발생");
			return "common/errorPage";
		}

		return "member/secession";
	}

}
