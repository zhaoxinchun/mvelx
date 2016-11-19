
用于提供mvel的中文注释版,并且删除在实际开发中不再使用的代码，提供一个简单的脚本执行引擎

#### 新开项目的缘由
一切为了开发人员能够看得懂框架代码，能够修改项目中的bug，能够修改轮子

1. 需要一个全新中文注释的项目，仅仅是翻译的话，可以参照我另一个开源项目 [mvel中文注释版](https://github.com/flym/mvel)
2. 对原mvel中的代码进行精简，使得使用的人都能看得懂主要的功能
3. 对原mvel中的代码进行整理，修复一些可能存在的bug，修改一些对开发人员理解不清晰的逻辑
4. 重新整理相应的测试用例，保证测试可用性

#### 适用场景
需要动态地进行解析字符串，并且传入参数，最终只需要配置好相应的表达式，就能够在运行期间进行直接执行，并且相应的性能
能够达到要求的场景

- 一个基本数据转换引擎，在业务中需要根据不同的转换表达式，使用不同的属性，不同的操作符进行计算，这些都可以配置为表达式
- 标准的切面表达式，如spring el所作的
- 支持全量java语法，支持方法调用，动态静态引用，如ognl所作的
- 支持function声明，自定义函数，然后在表达式中进行引用
- 性能优化，一次编译之后，后续直接优化调用，同一个表达式(执行时参数不同)不需要每次进行编译
- 性能进一步提升，支持优化为asm字节码直接执行

#### 删除的功能列表
可能你都没有听说过，但这确实是原mvel中所拥有的功能，在当前的开发中，被删除了。主要的原因还是我们只想提供一个满足基本需要，
并且用于特定的应用场景的引擎。

- shell支持，即在控制台输入表达式然后由mvel进行解释执行
- 模板支持，即如freemarker那样，使用特定的语法嵌入mvel表达式
- 解释执行，我们认为能在一个项目中稳定使用的脚本都是值得编译的，就像java 需要 -server参数一样运行