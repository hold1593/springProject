package com.kh.spring.notice.model.service;

import java.util.List;
import java.util.Map;

import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.notice.model.vo.Notice;

public interface NoticeService {

	/* 공지사항 게시글 수 조회용 Service(검색포함)
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	public abstract int getListCount(Map<String, String> map) throws Exception;

	/** 공지사항 목록 조회용 Service(검색 포함)
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	public abstract List<Notice> selectList(Map<String, String> map, PageInfo pInf) throws Exception;

	/** 공지사항 상세조회용 Service
	 * @param no
	 * @return notice
	 * @throws Exception
	 */
	public abstract Notice noticeDetail(int no) throws Exception;

	/** 공지사항 작성용 Service
	 * @param notice
	 * @return result
	 * @throws Exception
	 */
	public abstract int noticeInsert(Notice notice) throws Exception;

	/** 공지사항 삭제용 Service
	 * @param no
	 * @return result
	 * @throws Exception
	 */
	public abstract int noticeDelete(int no) throws Exception;

	/** 공지사항 수정 화면용 Service
	 * @param no
	 * @return notice
	 * @throws Exception
	 */
	public abstract Notice noticeUpdateForm(int no) throws Exception;

	/** 공지사항 수정용 Service
	 * @param notice
	 * @return result
	 * @throws Exception
	 */
	public abstract int noticeUpdate(Notice notice) throws Exception;

	
	
}
