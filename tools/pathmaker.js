$(function() {
  var $destination = $('#pathmaker-destination'),
      $environment = $('#pathmaker-environment');
  
  
  var multiplier = 2,  // pixels
      period     = 10; // ms
  
  
  var cursorX, cursorY;
  document.onmousemove = function(e){
      cursorX = e.pageX - $environment.offset().left;
      cursorY = e.pageY - $environment.offset().top;
  };
  
  
  function add() {
    $destination.html($destination.html() +
      "{" + cursorX*multiplier + ", " + cursorY*multiplier + "},\n");
  }
  
  
  var interval;
  $environment.on('mousedown', function(e) {
    clearInterval(interval);
    $destination.html('');
    interval = setInterval(add, period);
    
    add(e);
  });
  
  
  document.onmouseup = function() {
    clearInterval(interval);
  };
});