import java.util.Map;
import java.util.Set;

/**
 * 嵌套类应只为其封闭类(enclosing class)服务，如果在其他类中有调用，那么应设为顶层类。
 * 嵌套类有四种：静态成员类(static member class)、非静态成员类、匿名类和本地类(local class)。
 * 除第一种外，所有其他类型都被称为内部类。下面讲述何时使用哪种嵌套类，以及为什么使用。
 *
 * 静态成员类，最简单的嵌套类，最好看作“声明在其他类里面的普通类”，它可以访问其所在的类中的所有成员变量和方法，
 * private型也不例外。静态成员类可以看作其封闭类的静态成员，与其他静态成员遵循相同的可访问规则，
 * 比如声明private之后，就只能在该封闭类内部访问这样。
 *
 * 静态成员类的一种常见方法是作为public型helper类，协助其所在的外部类工作。比如设计计算器时，
 * 可以编写一个枚举类存放各种运算符选项。
 *
 * 静态成员类与非静态成员类之间虽然只差一个static关键字，但区别却很大。非静态成员类的每个实例中，
 * 实际上隐含了一个对其外部类的实例的引用，可以自由调用外部类的方法，也可通过this的构造方法获取外部类实例。
 *
 * 开始时提到过后三种类称为内部类，是因为它们无法独立于外部类存在；换句话说，如果要在外部类之外使用嵌套类，
 * 那么这个嵌套类一定是静态成员类。
 *
 *
 * 静态&非静态成员类
 * 非静态成员类的实例在创建时，通过外部类实例调用该类构造方法，同时与外部类创建关系，之后该关系无法被修改。
 * (也可以通过"外部类的实例.new 内部类名(args)"的方式手动建立联系，不过很少见)
 * 当然，这种关联的建立也需要占用空间并且增加构造用时。
 *
 * 非静态成员类的应用之一是编写Adapter, 它允许另一个不相干的类的实例使用adapter所在的外部类的实例。
 * 例如{@link java.util.Map}用{@link Map#keySet()}，{@link Map#entrySet()}，{@link Map#values()}
 * 等方法获取其集合视图。类似地，{@link java.util.List}与{@link java.util.Set}也用非静态成员类实现
 * {@link java.util.Iterator}迭代器功能。
 *
 * 如果嵌套类不需要访问外部类，可以用static关键字修饰一下，使它成为静态成员类。否则每个实例创建时，
 * 都默认有一个与其外部类实例无关的隐藏引用，占用时间内存之外，并且万一不符合GC的回收条件(见item 7)，
 * 就会造成内存泄漏。这种问题不用专门的泄漏检测软件就无法发现，很严重，必须重视。
 *
 * private static型成员类的一种常见用法是代表其外部类所表示的对象的组件。例如{@link Map}中，
 * 通过{@link java.util.Map.Entry}类的实例，方便地表示键值对。entry实例与map实例相关联，
 * entry中的方法(如{@link Map.Entry#getKey()}等)被调用时却无需访问map实例。因此用非静态成员来表示entry很浪费，
 * 最佳选择是private static型成员类。如果不小心忘记了static关键字，虽然entry映射仍然可以用，
 * 但每个entry都包含了对map的多余关联，浪费时间和内存。
 *
 * 如果相关的类是导出类的public或protected型成员，那么在是否使用static关键字问题上做出正确选择至关重要。
 * 在这种情况下，嵌套类是导出API的一部分，而且为了兼容，后续版本将不能够把它从非static改成static.
 *
 *
 * 匿名类
 * 匿名类没有名字，不是外部类的成员，也不与其他成员一起声明，而是在使用的过程中被声明和实例化。
 * 它可以存在于代码中任何允许表达式出现的地方。当且仅当匿名类出现在非静态环境中，才允许拥有外部类的实例；
 * 而且就算在静态环境中出现，它也不可以拥有除了常量之外的任何static型成员变量。
 *
 * 匿名类使用时有很多限制：除了声明的时候之外不能实例化、不能对其使用instanceof以及任何跟名字相关的操作、
 * 不能implements多个接口或者extends+implements这样、除从父类继承过来的外不能调用任何成员变量或成员方法。
 * 并且，由于匿名类往往跟表达式编写在一起，因此需要尽量简短，十行之内，否则会影响可读性。
 *
 * 在lambda表达式加入Java之前，匿名类是动态创建小型实例实现某一函数或进程对象的首选方法，
 * 但之后lambda表达式以其优势在这些方面取代了匿名类。
 *
 * 另外，在静态工厂方法的实现中，也会用到匿名类。(见item 20)
 *
 *
 * 本地类
 * 本地类使用最少。任何可以声明局部(local)变量的地方都可以声明本地类，并且所需遵守的规则也和局部变量一样。
 * 它有名字可以反复使用；但跟匿名类类似，只有在非静态环境中定义时才拥有外部类的实例，也不能拥有静态成员；
 * 而且它的名字应尽可能简短以免降低代码可读性。
 *
 * 综上所述，四种嵌套类各有所长。
 *      1.太长或者需要在方法外可见，推荐用成员类；成员类需要有对外部类实例的引用，
 *      就不要设置成静态的。
 *      2.如果嵌套类是某个方法的一部分，就用匿名类或者本地类；只用一次且其类型可用已有类型描述推荐匿名类，
 *      否则用本地类。
 *
 * @author LightDance
 */
public class FavorStaticMemberClasses {

}
