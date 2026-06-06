package com.educhallenge.challenges.dto;

public class ChallengeSocialSummaryResponse {

	private long likeCount;
	private long saveCount;
	private long shareCount;
	private long commentCount;
	private boolean likedByCurrentUser;
	private boolean savedByCurrentUser;
	private boolean completedByCurrentUser;
	private boolean canComment;

	public ChallengeSocialSummaryResponse() {
	}

	public ChallengeSocialSummaryResponse(
			long likeCount,
			long saveCount,
			long shareCount,
			long commentCount,
			boolean likedByCurrentUser,
			boolean savedByCurrentUser,
			boolean completedByCurrentUser,
			boolean canComment
	) {
		this.likeCount = likeCount;
		this.saveCount = saveCount;
		this.shareCount = shareCount;
		this.commentCount = commentCount;
		this.likedByCurrentUser = likedByCurrentUser;
		this.savedByCurrentUser = savedByCurrentUser;
		this.completedByCurrentUser = completedByCurrentUser;
		this.canComment = canComment;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public long getSaveCount() {
		return saveCount;
	}

	public void setSaveCount(long saveCount) {
		this.saveCount = saveCount;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	public long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(long commentCount) {
		this.commentCount = commentCount;
	}

	public boolean isLikedByCurrentUser() {
		return likedByCurrentUser;
	}

	public void setLikedByCurrentUser(boolean likedByCurrentUser) {
		this.likedByCurrentUser = likedByCurrentUser;
	}

	public boolean isSavedByCurrentUser() {
		return savedByCurrentUser;
	}

	public void setSavedByCurrentUser(boolean savedByCurrentUser) {
		this.savedByCurrentUser = savedByCurrentUser;
	}

	public boolean isCompletedByCurrentUser() {
		return completedByCurrentUser;
	}

	public void setCompletedByCurrentUser(boolean completedByCurrentUser) {
		this.completedByCurrentUser = completedByCurrentUser;
	}

	public boolean isCanComment() {
		return canComment;
	}

	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}
}
