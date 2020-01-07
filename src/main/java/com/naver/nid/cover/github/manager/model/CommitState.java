package com.naver.nid.cover.github.manager.model;

public enum CommitState {
	SUCCESS, FAILURE, PENDING, ERROR;

	public String toApiValue() {
		return this.name().toLowerCase();
	}
}
