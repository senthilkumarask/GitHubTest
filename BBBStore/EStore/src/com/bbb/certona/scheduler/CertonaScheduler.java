package com.bbb.certona.scheduler;

import javax.transaction.TransactionManager;

import atg.service.scheduler.SingletonSchedulableService;

import com.bbb.certona.manager.CertonaFeedManager;

public abstract class CertonaScheduler extends SingletonSchedulableService {
  /**
   * Transaction Manager instance for scheduler
   */
  private TransactionManager transactionManager;
  private String typeOfFeed;
  private boolean fullDataFeed;
  private boolean enabled;
  private CertonaFeedManager feedManager;

  /**
   * @return the transactionManager
   */
  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  /**
   * @param transactionManager
   *          the transactionManager to set
   */
  public void setTransactionManager(final TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * @return the feedManager
   */
  public CertonaFeedManager getFeedManager() {
    return feedManager;
  }

  /**
   * @param feedManager
   *          the feedManager to set
   */
  public void setFeedManager(final CertonaFeedManager feedManager) {
    this.feedManager = feedManager;
  }

  /**
   * @return the typeOfFeed
   */
  public String getTypeOfFeed() {
    return typeOfFeed;
  }

  /**
   * @param typeOfFeed
   *          the typeOfFeed to set
   */
  public void setTypeOfFeed(final String typeOfFeed) {
    this.typeOfFeed = typeOfFeed;
  }

  /**
   * @return the isFullDataFeed
   */
  public boolean isFullDataFeed() {
    return fullDataFeed;
  }

  /**
   * @param isFullDataFeed
   *          the isFullDataFeed to set
   */
  public void setFullDataFeed(final boolean fullDataFeed) {
    this.fullDataFeed = fullDataFeed;
  }

  /**
   * @return the isEnabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param isEnabled
   *          the isEnabled to set
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

}
