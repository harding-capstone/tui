open module com.shepherdjerred.capstone.tui {
  requires static lombok;
  requires com.shepherdjerred.capstone.logic;
  requires com.shepherdjerred.capstone.ai;
  requires com.shepherdjerred.capstone.storage;
  requires com.shepherdjerred.capstone.server;
  requires com.shepherdjerred.capstone.common;
  requires org.apache.logging.log4j;
  requires io.jenetics.base;
  exports com.shepherdjerred.capstone.tui;
}
