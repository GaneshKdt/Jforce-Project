//followed tutorial from javascript30.com but added options like clear drawing, change color and change brush size. 

// Took color picker from http://jscolor.com

//*************NOT TOUCH SUPPORTED

const canvas = document.querySelector('#draw');
const ctx = canvas.getContext('2d');
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;
ctx.strokeStyle = '#000';
ctx.lineJoin = 'round';
ctx.lineCap = 'round';

let hue = 0;
 
let isDrawing = false;
let lastX = 0;
let lastY = 0;

function draw(e){
  if(!isDrawing) return;
  // ctx.strokeStyle = `hsl(${hue},100%,50%)`;
  console.log(e.offsetY);
  ctx.beginPath();
  ctx.moveTo(lastX,lastY);
  ctx.lineTo(e.offsetX,e.offsetY);
  ctx.stroke();
  // ctx.lineWidth = 100;
  //destructing arrary 
  [lastX, lastY] = [e.offsetX, e.offsetY]
  hue++;
  // console.log('drawing');
}



canvas.addEventListener('mousedown', (e) => {
  isDrawing = true;
  [lastX, lastY] = [e.offsetX, e.offsetY];
});
canvas.addEventListener('mousemove', draw);
canvas.addEventListener('mouseup', () => isDrawing = false);
canvas.addEventListener('mouseout', () => isDrawing = false); 


canvas.addEventListener('touchstart', (e) => {
  isDrawing = true;
  [lastX, lastY] = [e.offsetX, e.offsetY];
});
canvas.addEventListener('touchmove', draw);
// canvas.addEventListener('touchend', () => isDrawing = false);
 document.getElementById('clear').addEventListener('click', function() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
      }, false);

// /* event listener */
document.getElementsByName("fontSize")[0].addEventListener('change', adjustFont);
 // <input type="input" id="color" name="color" value="0">

// /* function */
function adjustFont(){
  var fontSize = document.getElementsByName("fontSize")[0].value;
  console.log(fontSize);  
  ctx.lineWidth = fontSize;
 }

var options = {
				valueElement: null,
				width: 300,
				height: 120,
				sliderSize: 20,
				position: 'top',
				borderColor: '#fff',
				insetColor: '#fff',
				backgroundColor: '#202020'
			};

			var pickers = {};

			pickers.bgcolor = new jscolor('bgcolor-button', options);
			pickers.bgcolor.onFineChange = "update('bgcolor')";
			pickers.bgcolor.fromString('AB2567');
	    ctx.strokeStyle = pickers.bgcolor.toHEXString();
 

			function update (id) {
				ctx.strokeStyle =
					pickers.bgcolor.toHEXString();

				document.getElementById(id + '-rgb').value = pickers[id].toRGBString();
				document.getElementById(id + '-hex').value = pickers[id].toHEXString();

				document.getElementById(id + '-hue').value = Math.round(pickers[id].hsv[0]);
				document.getElementById(id + '-sat').value = Math.round(pickers[id].hsv[1]);
				document.getElementById(id + '-val').value = Math.round(pickers[id].hsv[2]);

				document.getElementById(id + '-red').value = Math.round(pickers[id].rgb[0]);
				document.getElementById(id + '-grn').value = Math.round(pickers[id].rgb[1]);
				document.getElementById(id + '-blu').value = Math.round(pickers[id].rgb[2]);
			}

			function setHSV (id, h, s, v) {
				pickers[id].fromHSV(h, s, v);
				update(id);
			}

			function setRGB (id, r, g, b) {
				pickers[id].fromRGB(r, g, b);
				update(id);
			}

			function setString (id, str) {
				pickers[id].fromString(str);
				update(id);
			}

			update('bgcolor');
			// update('fgcolor');

