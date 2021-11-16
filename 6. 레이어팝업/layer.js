/**
* 레이어 팝업 
*
 */
const layer = {
	/**
	* 레이어 팝업열기
	* 
	* @param url 팝업 URL
	* @param width 팝업 너비
	* @param height 팝업 높이  
	*/
	popup : function(url, width, height) {
		// layer_dim이 없는 경우만 동적으로 추가 
		if ($("#layer_dim").length == 0) { 
			$("body").append("<div id='layer_dim'></div>");			
		}
		
		// 배경 처리 
		$("#layer_dim").css({
			position: "fixed",
			width : "100%",
			height : "100%",
			background : "rgba(0, 0, 0, 0.6)",
			zIndex : 100,
			top: 0,
			left: 0,
			cursor: "pointer",
		});
		
		// 팝업 처리 
		if ($("#layer_popup").length == 0) { 
			$("body").append("<div id='layer_popup'><div id='html'></div></div>");	
		}
		
		const xpos = Math.round(($(window).width() - width) / 2);
		const ypos = Math.round(($(window).height() - height) / 2);
		
		$("#layer_popup").css({
			position: "fixed",
			width : width + "px",
			height : height + "px",
			top : ypos + "px",
			left : xpos + "px",
			background : "#ffffff",
			zIndex : 101,
			borderRadius: "20px",
			overflow: "auto",
			padding: "20px",
		});
		
		$.ajax({
			url : url,
			type : "GET",
			dataType : "html",
			success : function(res) {
				$("#layer_popup > #html").html(res);
			},
			error : function(err) {
				console.error(err);
			}
		});
	},
	/**
		레이어 팝업 닫기
	 */
	close : function() {
		$("#layer_dim, #layer_popup").remove();
	}
};

$(function() {
	$("body").on("click", "#layer_dim", function() {
		layer.close();	
	});
});