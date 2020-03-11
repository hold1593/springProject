package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.common.vo.PageInfo;

public interface BoardService {

	/**
	 * 전체 게시글 수 조회용 Service
	 * 
	 * @param map
	 * @return listCount
	 * @throws Exception
	 */
	public abstract int getListCount(Map<String, Object> map) throws Exception;

	/**
	 * 게시글 목록 조회용 Service
	 * 
	 * @param map
	 * @param pInf
	 * @return list
	 * @throws Exception
	 */
	public abstract List<Board> selectList(Map<String, Object> map, PageInfo pInf) throws Exception;

	/**
	 * 게시글 등록용 Service
	 * 
	 * @param board
	 * @param files
	 * @return result
	 * @throws Exception
	 */
	public abstract int insertBoard(Board board, List<Attachment> files) throws Exception;

	/**
	 * 썸네일 목록 조회용 Service
	 * 
	 * @param map
	 * @param pInf
	 * @return thList
	 * @throws Exception
	 */
	public abstract List<Attachment> selectThumbnailList(List<Board> list) throws Exception;

	/** 게시글 상세조회용 Service
	 * @param no
	 * @return board
	 * @throws Exception
	 */
	public abstract Board selectBoard(Integer no) throws Exception;

	/** 게시글 조회 하기
	 * @param no
	 * @return 
	 * @throws Exception
	 */
	public abstract List<Attachment> selectFiles(Integer no) throws Exception;

	/** 조회수 증가용 Service
	 * @param no
	 * @return result
	 * @throws Exception
	 */
	public abstract int increaseCount(Integer no) throws Exception;

	/** 게시글 수정용 Service
	 * @param board
	 * @param thumbnail
	 * @param images
	 * @param savePath
	 * @return result
	 * @throws Exception
	 */
	public abstract int updateBoard(Board board, MultipartFile thumbnail, List<MultipartFile> images, String savePath) throws Exception;

}
