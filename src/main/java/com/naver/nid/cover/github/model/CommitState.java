package com.naver.nid.cover.github.model;

public enum CommitState {
	SUCCESS, FAILURE, PENDING, ERROR;

	public String toApiValue() {
		return this.name().toLowerCase();
	}
}
