package org.mvel2.util;

import org.mvel2.ast.ASTNode;

import java.io.Serializable;

/**
 * 描述一个整个表达式的节点链式处理,即整个表达式是由多个节点一个一个串起来执行的.而整个链表,即可以从第1个节点开始,一个一个地往下进行执行
 * 直到完成整个节点迭代工作.
 * 因此,关于节点迭代的一些处理,可以由链式处理来完成,这里也完成一个链式处理的通用流程.在具体的处理流程当中,可以由节点处理逻辑来对链式本身进行
 * 反向的操作,比如删除当前节点,下一个节点等
 * 整个处理可以理解为一个字节点处理栈,可以跳跃,反复,或者直接返回等
 * The ASTIterator interface defines the functionality required by the enginer, for compiletime and runtime
 * operations.  Unlike other script implementations, MVEL does not use a completely normalized AST tree for
 * it's execution.  Instead, nodes are organized into a linear order and delivered via this iterator interface,
 * much like bytecode instructions.
 */
public interface ASTIterator extends Serializable {
  /** 重置,即回到第一个节点 */
  void reset();

  /** 返回并将下标指向下一个节点 */
  ASTNode nextNode();

  /** 获取下一个节点(执行状态不作处理) */
  ASTNode peekNext();

  /** 获取当前节点(执行状态不作处理) */
  ASTNode peekNode();

  /** 下标重新调整为last节点,即回到上一次执行的位置 */
  void back();

  /** 是否还有更多的节点要处理 */
  boolean hasMoreNodes();

  /** 返回第1个节点,当前状态不变化 */
  ASTNode firstNode();

  /** 当前节点链的长度 */
  int size();

  /**
   * 完成节点链处理,并重新调整相应的状态,回到初始状态
   * 与reset类似,但此方法会作一些节点整理操作,以优化节点处理
   */
  void finish();

  /** 在当前处理下标处添加新的节点 */
  void addTokenNode(ASTNode node);

  /** 在当前处理下标处理处添加2个新的节点 */
  void addTokenNode(ASTNode node1, ASTNode node2);

}
