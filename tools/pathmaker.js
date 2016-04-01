$(function() {
  var $destination = $('#pathmaker-destination'),
      $environment = $('#pathmaker-environment');
  
  
  var cursorX, cursorY;
  document.onmousemove = function(e){
      cursorX = e.pageX - $environment.offset().left;
      cursorY = e.pageY - $environment.offset().top;
  };
  
  
  function add() {
    $destination.html($destination.html() +
      "{" + cursorX + ", " + cursorY + "},\n");
  }
  
  
  var interval;
  $environment.on('mousedown', function(e) {
    clearInterval(interval);
    $destination.html('');
    interval = setInterval(add, 100);
    
    add(e);
  });
  
  
  document.onmouseup = function() {
    clearInterval(interval);
  };
});