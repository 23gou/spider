package com.spider.entity.base;

import java.util.Date;

import javax.persistence.Lob;

import com.common.jdbc.BaseEntity;

/**
 * 
 * 
 * 描述:抓取结果
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:17:13
 */
@javax.persistence.MappedSuperclass
public class BaseRobotResult extends BaseEntity {
	private static final long serialVersionUID = -7948930413157445187L;

	public BaseRobotResult() {
	}

	/**
	 * 明星
	 */
	private Long starId;

	/**
	 * 分类ID
	 */
	private Long categoryId;

	/**
	 * 任务
	 */
	private Long taskId;

	/**
	 * 开始时间
	 */
	private Date startDateTime;

	/**
	 * 进度
	 */
	private String ResultStatus;

	/**
	 * 编辑状态，编辑后，将会进行排名
	 */
	private String editStatus;
	/**
	 * 微信文章数量
	 */
	private Integer wechatNumber = 0;
	/**
	 * 微信排名
	 */
	private Integer wechatRank = 0;
	/**
	 * 微博转发数
	 */
	private Integer weiboForward = 0;

	/**
	 * 微博转发数
	 */
	private Integer weiboForwardRank = 0;

	/**
	 * 微博评论
	 */
	private Integer weiboComment = 0;
	/**
	 * 微博转发数
	 */
	private Integer weiboCommentRank = 0;

	/**
	 * 微博点赞数
	 */
	private Integer weiboLinkStatus = 0;
	/**
	 * 微博转发数
	 */
	private Integer weiboLinkStatusRank = 0;

	/**
	 * 微博粉丝数
	 */
	private Integer weiboFan = 0;
	/**
	 * 微博粉丝增量
	 */
	private Integer weiboFanInc = 0;
	/**
	 * 微博粉丝数排名
	 */
	private Integer weiboFanIncRank = 0;
	/**
	 * 微博提及量
	 */
	private Integer weiboData = 0;
	
	/**
	 * 微博提及量排行
	 */
	private Integer weiboDataRank = 0;
	/**
	 * 贴吧签到数
	 */
	private Integer tiebaSign = 0;
	/**
	 * 贴吧签到排行
	 */
	private Integer tiebaSignRank = 0;
	/**
	 * 贴吧帖子数
	 */
	private Integer tiebaPostNum = 0;
	/**
	 * 贴吧帖子数增量
	 */
	private Integer tiebaPostNumInc = 0;
	/**
	 * 贴吧帖子数增量排名
	 */
	private Integer tiebaPostNumIncRank = 0;
	/**
	 * 贴吧会员数
	 */
	private Integer tiebaMemberNum = 0;
	/**
	 * 贴吧会员数增量
	 */
	private Integer tiebaMemberNumInc = 0;
	/**
	 * 贴吧会员数增量排行
	 */
	private Integer tiebaMemberNumIncRank = 0;

	/**
	 * 百度新闻数量
	 */
	private Integer baiduNews = 0;
	/**
	 * 百度新闻数量排行
	 */
	private Integer baiduNewsRank = 0;
	/**
	 * 百度新闻数量图片
	 */
	@Lob
	private String baiduIndexImg;
	/**
	 * 百度新闻数量
	 */
	private Integer baiduIndex = 0;
	/**
	 * 百度新闻数量排行
	 */
	private Integer baiduIndexRank = 0;
	/**
	 * 综合得分
	 */
	private Integer score = 0;

	/**
	 * 综合排名
	 */
	private Integer scoreRank = 0;

	public Long getStarId() {
		return starId;
	}

	public void setStarId(Long starId) {
		this.starId = starId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Integer getWechatNumber() {
		return wechatNumber;
	}

	public void setWechatNumber(Integer wechatNumber) {
		this.wechatNumber = wechatNumber;
	}

	public Integer getWechatRank() {
		return wechatRank;
	}

	public void setWechatRank(Integer wechatRank) {
		this.wechatRank = wechatRank;
	}

	public String getResultStatus() {
		return ResultStatus;
	}

	public void setResultStatus(String resultStatus) {
		ResultStatus = resultStatus;
	}

	public Integer getWeiboForward() {
		return weiboForward;
	}

	public void setWeiboForward(Integer weiboForward) {
		this.weiboForward = weiboForward;
	}

	public Integer getWeiboForwardRank() {
		return weiboForwardRank;
	}

	public void setWeiboForwardRank(Integer weiboForwardRank) {
		this.weiboForwardRank = weiboForwardRank;
	}

	public Integer getWeiboComment() {
		return weiboComment;
	}

	public void setWeiboComment(Integer weiboComment) {
		this.weiboComment = weiboComment;
	}

	public Integer getWeiboCommentRank() {
		return weiboCommentRank;
	}

	public void setWeiboCommentRank(Integer weiboCommentRank) {
		this.weiboCommentRank = weiboCommentRank;
	}

	public Integer getWeiboLinkStatus() {
		return weiboLinkStatus;
	}

	public void setWeiboLinkStatus(Integer weiboLinkStatus) {
		this.weiboLinkStatus = weiboLinkStatus;
	}

	public Integer getWeiboLinkStatusRank() {
		return weiboLinkStatusRank;
	}

	public void setWeiboLinkStatusRank(Integer weiboLinkStatusRank) {
		this.weiboLinkStatusRank = weiboLinkStatusRank;
	}

	public Integer getWeiboFan() {
		return weiboFan;
	}

	public void setWeiboFan(Integer weiboFan) {
		this.weiboFan = weiboFan;
	}

	public Integer getWeiboFanInc() {
		return weiboFanInc;
	}

	public void setWeiboFanInc(Integer weiboFanInc) {
		this.weiboFanInc = weiboFanInc;
	}

	public Integer getWeiboFanIncRank() {
		return weiboFanIncRank;
	}

	public void setWeiboFanIncRank(Integer weiboFanIncRank) {
		this.weiboFanIncRank = weiboFanIncRank;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getWeiboDataRank() {
		return weiboDataRank;
	}

	public void setWeiboDataRank(Integer weiboDataRank) {
		this.weiboDataRank = weiboDataRank;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Integer getTiebaSign() {
		return tiebaSign;
	}

	public void setTiebaSign(Integer tiebaSign) {
		this.tiebaSign = tiebaSign;
	}

	public Integer getTiebaSignRank() {
		return tiebaSignRank;
	}

	public void setTiebaSignRank(Integer tiebaSignRank) {
		this.tiebaSignRank = tiebaSignRank;
	}

	public Integer getTiebaPostNum() {
		return tiebaPostNum;
	}

	public void setTiebaPostNum(Integer tiebaPostNum) {
		this.tiebaPostNum = tiebaPostNum;
	}

	public Integer getTiebaPostNumInc() {
		return tiebaPostNumInc;
	}

	public void setTiebaPostNumInc(Integer tiebaPostNumInc) {
		this.tiebaPostNumInc = tiebaPostNumInc;
	}

	public Integer getTiebaPostNumIncRank() {
		return tiebaPostNumIncRank;
	}

	public void setTiebaPostNumIncRank(Integer tiebaPostNumIncRank) {
		this.tiebaPostNumIncRank = tiebaPostNumIncRank;
	}

	public Integer getTiebaMemberNum() {
		return tiebaMemberNum;
	}

	public void setTiebaMemberNum(Integer tiebaMemberNum) {
		this.tiebaMemberNum = tiebaMemberNum;
	}

	public Integer getTiebaMemberNumInc() {
		return tiebaMemberNumInc;
	}

	public void setTiebaMemberNumInc(Integer tiebaMemberNumInc) {
		this.tiebaMemberNumInc = tiebaMemberNumInc;
	}

	public Integer getTiebaMemberNumIncRank() {
		return tiebaMemberNumIncRank;
	}

	public void setTiebaMemberNumIncRank(Integer tiebaMemberNumIncRank) {
		this.tiebaMemberNumIncRank = tiebaMemberNumIncRank;
	}

	public Integer getBaiduNews() {
		return baiduNews;
	}

	public void setBaiduNews(Integer baiduNews) {
		this.baiduNews = baiduNews;
	}

	public Integer getBaiduNewsRank() {
		return baiduNewsRank;
	}

	public void setBaiduNewsRank(Integer baiduNewsRank) {
		this.baiduNewsRank = baiduNewsRank;
	}

	public String getBaiduIndexImg() {
		return baiduIndexImg;
	}

	public void setBaiduIndexImg(String baiduIndexImg) {
		this.baiduIndexImg = baiduIndexImg;
	}

	public Integer getBaiduIndex() {
		if (baiduIndex == null) {
			baiduIndex = 0;
		}

		return baiduIndex;
	}

	public void setBaiduIndex(Integer baiduIndex) {
		this.baiduIndex = baiduIndex;
	}

	public Integer getBaiduIndexRank() {
		return baiduIndexRank;
	}

	public void setBaiduIndexRank(Integer baiduIndexRank) {
		this.baiduIndexRank = baiduIndexRank;
	}

	public String getEditStatus() {
		return editStatus;
	}

	public void setEditStatus(String editStatus) {
		this.editStatus = editStatus;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScoreRank() {
		return scoreRank;
	}

	public void setScoreRank(Integer scoreRank) {
		this.scoreRank = scoreRank;
	}

	public Integer getWeiboData() {
		return weiboData;
	}

	public void setWeiboData(Integer weiboData) {
		this.weiboData = weiboData;
	}
}
