package com.kh.spring.notice.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.notice.model.dao.NoticeDAO;
import com.kh.spring.notice.model.vo.Notice;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService{

	@Autowired
	private NoticeDAO noticeDAO;

	/* 공지사항 게시글 수 조회용 Service(검색포함)
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	@Override
	public int getListCount(Map<String, String> map) throws Exception {
		
		return noticeDAO.getListCount(map);
	}
	
	/** 공지사항 목록 조회용 Service(검색 포함)
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	@Override
	public List<Notice> selectList(Map<String, String> map, PageInfo pInf) throws Exception {
		return noticeDAO.selectList(map,pInf);
	}

	/** 공지사항 상세조회용 Service
	 * @param no
	 * @return notice
	 * @throws Exception
	 */
	@Transactional
	@Override
	public Notice noticeDetail(int no) throws Exception {
		
		Notice notice = noticeDAO.noticeDetail(no);
		if(notice !=null) {
			noticeDAO.noticeCount(notice);
		}
		
		return notice;
		
	}

	/** 공지사항 작성용 Service
	 * @param notice
	 * @return result
	 * @throws Exception
	 */
	@Transactional
	@Override
	public int noticeInsert(Notice notice) throws Exception {
		
		return noticeDAO.noticeInsert(notice);
	}
	
	/** 공지사항 삭제용 Service
	 * @param no
	 * @return result
	 * @throws Exception
	 */
	@Transactional
	@Override
	public int noticeDelete(int no) throws Exception {
		
		return noticeDAO.noticeDelete(no);
	}

	/** 공지사항 수정 화면용 Service
	 * @param no
	 * @return notice
	 * @throws Exception
	 */
	@Override
	public Notice noticeUpdateForm(int no) throws Exception {
		
		return noticeDAO.noticeDetail(no);
	}

	/** 공지사항 수정용 Service
	 * @param notice
	 * @return result
	 * @throws Exception
	 */
	@Transactional
	@Override
	public int noticeUpdate(Notice notice) throws Exception {
		
		return noticeDAO.noticeUpdate(notice);
	}
}
