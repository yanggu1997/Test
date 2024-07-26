/* 自定义trim */
function trim (str) {  //删除左右两端的空格,自定义的trim()方法
  return str == undefined ? "" : str.replace(/(^\s*)|(\s*$)/g, "")
}

//获取url地址上面的参数
function requestUrlParam(argname){
  var url = location.href//获取完整的请求url路径
  var arrStr = url.substring(url.indexOf("?")+1).split("&")
    //截取问号后面第一个部分http://localhost:8080/backend/page/member/add.html?id=1805611876464668673中的id=1805611876464668673
    //并且到&截止，避免url后面还有其他等式

    //将string字符串数组中找到对应的
    for(var i =0;i<arrStr.length;i++)
  {
      var loc = arrStr[i].indexOf(argname+"=")
      if(loc!=-1){
          return arrStr[i].replace(argname+"=","").replace("?","")
      }
  }
  return ""
}
