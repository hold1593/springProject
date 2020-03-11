package com.kh.spring.notice.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.common.vo.PageInfo;
import com.kh.spring.notice.model.vo.Notice;

@Repository("noticeDAO")
public class NoticeDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	/** 공지사항 게시글 수 조회용 DAO
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	public int getListCount(Map<String, String> map) throws Exception{
		return sqlSession.selectOne("noticeMapper.getListCount",map);
	}

	/** 공지사항 목록 조회용 DAO
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	public List<Notice> selectList(Map<String, String> map, PageInfo pInf) throws Exception{
		
		int offset = (pInf.getCurrentPage()-1) * pInf.getLimit();
		// 현재페이지 -1 한것에서 limit 만큼 건너뜀 
		RowBounds rowBounds = new RowBounds(offset, pInf.getLimit());
		return sqlSession.selectList("noticeMapper.selectList",map,rowBounds);
	}

	public Notice noticeDetail(int noticeNo) throws Exception{
		return sqlSession.selectOne("noticeMapper.selectDetail",noticeNo);
	}

	public int noticeCount(Notice notice) throws Exception{

		return sqlSession.update("noticeMapper.updateCount",notice);
		
	}

	public int noticeInsert(Notice notice) throws Exception{
		
		return sqlSession.insert("noticeMapper.noticeInsert",notice);
	}

	public int noticeDelete(int no) throws Exception{
		
		return sqlSession.update("noticeMapper.noticeDelete",no);
	}

	public int noticeUpdate(Notice notice) throws Exception{
		
		return sqlSession.update("noticeMapper.noticeUpdate",notice);
	}

	
	
	
	
}
