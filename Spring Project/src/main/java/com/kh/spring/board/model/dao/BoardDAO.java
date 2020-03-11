package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.vo.PageInfo;

/**
 * @author user1
 *
 */
@Repository
public class BoardDAO {
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	
	/** 전체 게시글 수 조회용 DAO
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	public int getListCount(Map<String, Object> map) throws Exception{
		
		return sqlSession.selectOne("boardMapper.getListCount",map);
	}

	/** 게시글 목록 조회용 DAO
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	public List<Board> selectList(Map<String, Object> map, PageInfo pInf) throws Exception{
		
		int offset = (pInf.getCurrentPage()-1)*pInf.getLimit();
		RowBounds rowBounds = new RowBounds(offset, pInf.getLimit());
		return sqlSession.selectList("boardMapper.selectList",map,rowBounds);
	}

	public int selectNextNo() throws Exception{
		return sqlSession.selectOne("boardMapper.selectNextNo");
	}

	
	/** 게시글 삽입용 DAO
	 * @param board
	 * @return result
	 * @throws Exception
	 */
	public int insertBoard(Board board) throws Exception{
		return sqlSession.insert("boardMapper.insertBoard",board);
	}

	/** 파일 삽입용 DAO
	 * @param at
	 * @return result
	 * @throws Exception
	 */
	public int insertAttachment(Attachment at) throws Exception{
		return sqlSession.insert("boardMapper.insertAttachment",at);
	}


	public List<Attachment> selectThumbnailList(List<Board> list) throws Exception{
		return sqlSession.selectList("boardMapper.selectThumbnailList",list);
	
	}

	/** 게시글 상세 조회용 DAO
	 * @param no
	 * @return board
	 * @throws Exception
	 */
	public Board selectBoard(Integer no) throws Exception {
		return sqlSession.selectOne("boardMapper.selectBoard",no);
	}

	/**
	 * @param no
	 * @return files
	 * @throws Exception
	 */
	public List<Attachment> selectFiles(Integer no) throws Exception{
		return sqlSession.selectList("boardMapper.selectFiles",no);
	}

	public int increaseCount(Integer no) throws Exception {
		return sqlSession.update("boardMapper.increaseCount",no);
	}

	/** 게시글 수정 DAO
	 * @param board
	 * @return result
	 * @throws Exception
	 */
	public int updateBoard(Board board) throws Exception{
		return sqlSession.update("boardMapper.updateBoard",board);
	}

	/** 파일 수정용 DAO
	 * @param at
	 * @return result
	 * @throws Exception
	 */
	public int updateAttachment(Attachment at) throws Exception{
		return sqlSession.update("boardMapper.updateAttachment",at);
	}

}
