package org.mvel2.util;

import org.mvel2.ast.ASTNode;

/** 一个具体的节点执行链表实现 */
public class ASTLinkedList implements ASTIterator {
  /** 第一个节点 */
  private ASTNode firstASTNode;
  /** 当前正在处理的节点 */
  private ASTNode current;
  /** 最后一次处理的节点 */
  private ASTNode last;
  /** 当前节点链的长度 */
  private int size;

  public ASTLinkedList() {
  }

  /** 基于一个节点来进行构建,此节点即为第1个节点 */
  public ASTLinkedList(ASTNode firstASTNode) {
    this.current = this.firstASTNode = firstASTNode;
  }

  /** 添加新的节点 */
  public void addTokenNode(ASTNode astNode) {
    //长度+1
    size++;

    //简单是当作第1个节点,还是进行追加
    //在调用此方法时,一般认为current就是最新的节点,因此不存在丢弃原节点的问题,即current.next认为原来就是null
    if (this.firstASTNode == null) {
      this.firstASTNode = this.current = astNode;
    }
    else {
      this.last = this.current = (this.current.nextASTNode = astNode);
    }
  }

  /** 入栈2个节点,按照先后顺序，第1个节点在前面，第2个节点在后面 */
  public void addTokenNode(ASTNode astNode, ASTNode token2) {
    //长度+2
    size += 2;

    //简单是当作第1个节点,还是进行追加,追加时按照参数从依次追加处理
    //在调用此方法时,一般认为current就是最新的节点,因此不存在丢弃原节点的问题,即current.next认为原来就是null
    if (this.firstASTNode == null) {
      this.last = this.current = ((this.firstASTNode = astNode).nextASTNode = token2);
    }
    else {
      this.last = this.current = (this.current.nextASTNode = astNode).nextASTNode = token2;
    }
  }

  public ASTNode firstNode() {
    return firstASTNode;
  }

  /** 返回当前链中是否只有一个节点 */
  public boolean isSingleNode() {
    //要么只有一个节点, 要么有2个节点,但第1个节点为调试节点
    return size == 1 || (size == 2 && firstASTNode.fields == -1);
  }

  /** 返回第1个节点, 如果第1个节点是调试节点,则返回下一个 */
  public ASTNode firstNonSymbol() {
    if (firstASTNode.fields == -1) {
      return firstASTNode.nextASTNode;
    }
    else {
      return firstASTNode;
    }
  }

  public void reset() {
    this.current = firstASTNode;
  }

  public boolean hasMoreNodes() {
    return this.current != null;
  }

  public ASTNode nextNode() {
    if (current == null) return null;
    try {
      return current;
    }
    finally {
      last = current;
      current = current.nextASTNode;
    }
  }

  public ASTNode peekNext() {
    if (current != null && current.nextASTNode != null)
      return current.nextASTNode;
    else
      return null;
  }


  public ASTNode peekNode() {
    if (current == null) return null;
    return current;
  }

  public void back() {
    current = last;
  }

  public int size() {
    return size;
  }

  /** 去掉中间被废掉的节点重新处理整个调用链 */
  public void finish() {
    reset();

    ASTNode last = null;
    ASTNode curr;

    //即遍列整个链,将中间被废弃掉的节点忽略不处理,使用next指针来进行重建
    while (hasMoreNodes()) {
      if ((curr = nextNode()).isDiscard()) {
        if (last == null) {
          last = firstASTNode = nextNode();
        }
        else {
          last.nextASTNode = nextNode();
        }
        continue;
      }

      if (!hasMoreNodes()) break;

      last = curr;
    }

    this.last = last;

    reset();
  }

}
