<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<!-- Credits for icons from http://www.fatcow.com/free-icons/ under http://creativecommons.org/licenses/by/3.0/us/-->
<html xmlns:v="urn:schemas-microsoft-com:vml">
   <head>
      <title>webgl-sprint</title>

      <script src="http://scenejs.org/api/latest/scenejs.js"></script>

      <!-- link extjs 4 -->
      <link rel="stylesheet" type="text/css" href="js/ext-4.1.1a/resources/css/ext-all.css">
      <script type="text/javascript" src="js/ext-4.1.1a/ext-all.js"></script>
      
      <!-- Example AJAX: Feel free to delete this -->
      <script type="text/javascript">
      Ext.application({
          name : 'portal',

          //Here we build our GUI from existing components - this function should only be assembling the GUI
          //Any processing logic should be managed in dedicated classes - don't let this become a
          //monolithic 'do everything' function
          launch : function() {
              
              //This is the actual function request to the backend. The rest is just ExtJS boilerplate.
              Ext.Ajax.request({
           	      url : 'getBoreholes.do',
           	      params : {
           	          maxFeatures : 1,
           	          boreholeId : 'borehole.123'
           	      },
           	      callback : function(options, success, response) {
           	          if (!success) {
           	              console.log('couldnt hit backend');
           	              return;
           	          }
           	          
           	          var responseJsonObject = Ext.JSON.decode(response.responseText);
           	          console.log("Got a response object that looks like: ", responseJsonObject);
           	      }
              });
          }
      });
      </script>
      
   </head>

   <body>
      <p>Hello World!</p>
   </body>

</html>