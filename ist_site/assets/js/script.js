/**
 *    Created by David Luong
 */

$(document).ready(function(){

/** AJAX Calls **/

  // Render content in the '#about' section
      renderData('get', {path: '/about/'}, '#about').done(function(json) {
          var container = $('<div></div>').attr('id', 'about');
          var title = $('<h2></h2>').attr('id', 'slogan').text(json.title);
          var desc   = $('<p></p>').text(json.description).append('<br>');
          var quote  = $('<p></p>').text('\"'+json.quote+'\"').css('font-style', 'italic');
          var author = $('<p></p>').text('-- '+json.quoteAuthor);

          $(container).append(title, '<br>', desc, '<br>', quote, '<br>', author);
          $('#cover').append('<br>', container);
      });


  // Render content in the '#degrees' section
      renderData('get', {path: '/degrees/'}, '#degrees').done(function(json) {
          var undergradContentCell = $('<div></div>').attr({class: 'content-cell degrees-content-cell', id: 'undergraduate-cell'});
          var gradContentCell = $('<div></div>').attr({class: 'content-cell degrees-content-cell', id: 'graduate-cell'});
          // var undergradTitle = $('<h2></h2>').text('Our Undergraduate Degrees');
          // var gradTitle = $('<h2></h2>').text('Our Graduate Degrees');
          var undergradInfo = $('<p></p>').attr('class', 'undergradInfo');
          var gradInfo = $('<p></p>').attr('class', 'gradInfo');
          var degreeName, title, degDesc, degCtr, advCert;

          //$('#degrees-content').append(undergradTitle);
          $(undergradContentCell).append('<br>');

          $.each(json.undergraduate, function(a, underDeg){
              degreeName = underDeg.degreeName;
              title = $('<h4></h4>').append(underDeg.title);
              degDesc = underDeg.description;
              degCtr = $('<p></p>').append(underDeg.concentrations);

              // $(undergradContentCell).append(title, "<br>", degDesc, "<br>", degCtr, "<br>");
              $(undergradInfo).append(title, degDesc, '<br>', degCtr, '<br>');

              // $('#degrees-content').each(function(i, val) {
                $(undergradContentCell).append(undergradInfo);

              // });

          });
          $('#degrees-content').append(undergradContentCell);

          $(gradContentCell).append('<br>');
          // $('#degrees-content').append("<br>", gradTitle);

          $.each(json.graduate, function(a, gradDeg){
              degreeName = gradDeg.degreeName;
              title = $('<h4></h4>').append(gradDeg.title);
              degDesc = gradDeg.description;
              degCtr = $('<p></p>').append(gradDeg.concentrations);
              advCert = $('<p></p>').append(gradDeg.availableCertificates);

              // $(gradInfo).append(title, "<br>", degDesc, "<br>", degCtr, "<br>");
              if(gradDeg.degreeName!='graduate advanced certificates') {
                $(gradInfo).append(title, degDesc, '<br>', degCtr, '<br>');
              }

              // $('#degrees-content').each(function(i, val) {
                $(gradContentCell).append(gradInfo);

              // });

              // $(gradContentCell).append(title, "<br>", degDesc, "<br>", degCtr, "<br>");

              // $(gradContentCell).append(gradInfo);
          });
          $('#degrees-content').append(gradContentCell);

          // $('#degrees-content').append(undergradTitle, undergradInfo, '<br>', gradTitle, gradInfo);

          // $('#degrees-content').append(undergradTitle, undergradContentCell, '<br>', gradTitle, gradContentCell);

      });


  // Render content in the '#minors' section
      renderData('get', {path: '/minors/'}, '#minors').done(function(json) {
          var minorTitle = $('<h2></h2>').text('Our Undergraduate Minors');
          var minors = $('<p></p>').attr('class', 'minors');
          var minName, minTitle, minDesc, minCourses;

          $.each(json.UgMinors, function(b, mnr) {
              minName = mnr.name;
              minTitle = mnr.title;
              minDesc = mnr.description;
              minCourses = mnr.courses;

              $(minors).append(minName, '<br>', minTitle, '<br>', minDesc, '<br>', '${minCourses}\n');
              //$('.minors-content-cell').append(minors);
          });

          // $('#minors').append(minorTitle, '<br>', minors);
          $('#minors-content-container').before(minorTitle, '<br>', minors);
      });


  // Render content in the '#employment' section
      renderData('get', {path: '/employment/'}, '#employment').done(function(json) {
          var empTitle = $('<h2></h2>').text(json.introduction.title);
          var empContent = $('<p></p>').attr('class','employment-content');
          var empContentTitle, empContentDesc, degreeStats;

          // var coopTable = $('<table></table>').attr('class':'emp-table','id':'coop_id');
          // coopTable.append('<thead><tr></tr></thead><tbody></tbody>');
          //
          // var empTable = $('<table></table>').attr('class':'emp-table','id':'emp_id');
          // empTable.append('<thead><tr></tr></thead><tbody></tbody>');

          $('.coop-tab > button').text(json.coopTable.title);
          $('.employment-tab > button').text(json.employmentTable.title);

          $.each(json.introduction.content, function(c, empCont){
              empContentTitle = empCont.title;
              empContentDesc = empCont.description;

              $(empContent).append(empContentTitle, '<br>', empContentDesc, '<br>');
          });

          $.each(json.degreeStatistics.statistics, function(d, empVal) {
              degreeStats = $('<p></p>').text(empVal.value).append('<br>', empVal.description);

              $(empContent).append(degreeStats);
          });

        //TO-DO: Add the map (<iframe src='http://ist.rit.edu/api/map.php' scrolling='no'>)

        //TO-DO:  2-column grid for {'Employers' names} and {'Career' names}
          //Node:
            //Employers - title, employerNames
            //Careers - title, careerNames

        //TO-DO: Either provide link to the tables, or use a 2-column grid (just the tabs)
          //TO-DO:  New container for 'coopTable'.
            //Node: title, coopInformation
              // coopInformation - employer, degree, city, term

          //TO-DO:  New container for 'employmentTable'.
            //Node: title, professionalEmploymentInformation
              // professionalEmploymentInformation - employer, degree, city, title, startDate

              /*
                coop_content_cell.append('<br>')
                  $.each (coops )
                    coop_info.append( coop_attributes )
                    coop_content_cell.append (coop_info )

                  coop_content.append ( coop_content_cell )

                emp_content_cell.append('<br>')
                  $.each ( employers )
                    emp_info.append( emp_attributes )
                    emp_content_cell.append (emp_info )

                  emp_content.append ( emp_content_cell )
              */

          $('#employment-tab-container').before(empTitle, '<br>', empContent, '<br>');
          //$('#employment').append('<br>', empContent);

      });


  // Render content in the '#people' section
      renderData('get', {path: '/people/'}, '#people').done(function(json) {
          var pplTitle = $('<h2></h2>').text(json.title);
          var pplSubTitle = $('<h4></h4>').text(json.subtitle);
          var facInfo = $('<h2></h2>').text('Faculty');
          var staffInfo = $('<h2></h2>').text('Staff');
          var indivFac = $('<div></div>').attr('class','faculties');
          var indivFacPopUp = $('<div></div>').attr('class','faculties-popup');
          var indivSt = $('<div></div>').attr('class','staffs');
          var indivStPopUp = $('<div></div>').attr('class','staffs-popup');
          var username, name, tagline, imgPath, ppTitle, intArea, office, ppWeb, ppPhone, ppEmail, ppTwitter, ppFacebook;

          //  Iterate over 'faculty' list
          $.each(json.faculty, function(i, fa) {
              username = fa.username;
              name = fa.name;
              tagline = fa.tagline;
              // imgPath = '<a class="faculty-images" href="'+fa.imagePath+'" data-featherlight="image"><img src="'+fa.imagePath+'" alt="Photo of '+fa.name+'"/></a>';
              imgPath = '<a class="people-images faculty-images" href="#" data-featherlight=".faculties-popup"><img src="'+fa.imagePath+'" alt="Photo of '+fa.name+'"/></a>';
              img = '<img src="'+fa.imagePath+'" alt="Photo of '+fa.name+'"/>';
              // imgPath = fa.imagePath;
              ppTitle = fa.title;
              intArea = fa.interestArea;
              office = fa.office;
              ppWeb = fa.website;
              ppPhone = fa.phone;
              ppEmail = fa.email;
              ppTwitter = fa.twitter;
              ppFacebook = fa.facebook;

              indivFac.append(imgPath);
              indivFacPopUp.append(
                  img, '<br>',
                  name, '<br>',
                  username, '<br>',
                  // tagline, '<br>',
                  ppTitle, '<br>',
                  intArea, '<br>',
                  office, '<br>',
                  ppWeb, '<br>',
                  ppPhone, '<br>',
                  ppEmail, '<br>',
                  ppTwitter, '<br>',
                  ppFacebook, '<br>'
              );
              // indivFacPopUp.append(
              //     imgPath, '<br>',
              //     name, '<br>',
              //     username, '<br>',
              //     tagline, '<br>',
              //     ppTitle, '<br>',
              //     intArea, '<br>',
              //     office, '<br>',
              //     ppWeb, '<br>',
              //     ppPhone, '<br>',
              //     ppEmail, '<br>',
              //     ppTwitter, '<br>',
              //     ppFacebook, '<br>'
              // );
          });

          //  Iterate over 'staff' list
          $.each(json.staff, function(j, st) {
              username = st.username;
              name = st.name;
              tagline = st.tagline;
              // imgPath = st.imagePath;
              imgPath = '<a class="people-images staff-images" href="#" data-featherlight=".staffs-popup"><img src="'+st.imagePath+'" alt="Photo of '+st.name+'"/></a>';
              img = '<img src="'+st.imagePath+'" alt="Photo of '+st.name+'"/>';
              ppTitle = st.title;
              intArea = st.interestArea;
              office = st.office;
              ppWeb = st.website;
              ppPhone = st.phone;
              ppEmail = st.email;
              ppTwitter = st.twitter;
              ppFacebook = st.facebook;

              indivSt.append(imgPath);
              indivStPopUp.append(
                  img, '<br>',
                  name, '<br>',
                  username, '<br>',
                  tagline, '<br>',
                  ppTitle, '<br>',
                  intArea, '<br>',
                  office, '<br>',
                  ppWeb, '<br>',
                  ppPhone, '<br>',
                  ppEmail, '<br>',
                  ppTwitter, '<br>',
                  ppFacebook, '<br>'
              );
          });

          $('#people-tab-container').before(pplTitle, '<br>');
          // $('#people').append('<br>', pplSubTitle, '<br>', facInfo, indivFac, '<br>', staffInfo, indivSt);
          $('#people').append('<br>', pplSubTitle, '<br>', facInfo, indivFac, '<br>', staffInfo, indivSt);

          //$(pplTitle).before('#people-tab-container');
      });


  // Render content in the '#research' section
      renderData('get', {path: '/research/'}, '#research').done(function(json) {
          var resTitle = $('<h2></h2>').text('Our Research');
          var intTitle = $('<h2></h2>').text('By Interest Area');
          var factTitle = $('<h2></h2>').text('By Faculty');
          var intResearch = $('<div></div>').attr('class','research-areas');
          var facResearch = $('<div></div>').attr('class','faculty-research-areas');
          var areaName, citations, rsFacName, rsFacUsername, rsCitations;

          $.each(json.byInterestArea, function(l, res) {
              areaName = res.areaName;
              citations = res.citations;

              intResearch.append(
                  areaName, '<br>',
                  citations, '<br>'
              );
          });

          $.each(json.byFaculty, function(m, rs) {
              rsFacName = rs.facultyName;
              rsFacUsername = rs.username;
              rsCitations = rs.citations;

              facResearch.append(
                rsFacName, '<br>',
                rsFacUsername, '<br>',
                rsCitations, '<br>'
              );
          });

          $('#research-tab-container').before(resTitle, '<br>');
          $('#research').append('<br><br>', intTitle, intResearch, '<br>', factTitle, facResearch);
      });


  // Render content in the '#resources' section
      renderData('get', {path: '/resources/'}, '#resources').done(function(json) {

      });

    //TO-DO: Move to top of the page ???
  // Render content in the '#news' section
      renderData('get', {path: '/news/'}, '#news').done(function(json) {

      });


  // Render content in the page's footer
      renderData('get', {path: '/footer/'}, '.page-footer').done(function(json) {
          var title     = $('<h2></h2>').text(json.social.title);
          var tweet     = $('<h3></h3>').text(json.social.tweet + " " + json.social.by).css({color: 'red', fontStyle: 'italic'});
          var animator  = $('<marquee></marquee>').attr({direction: 'left', scrollamount: '5', width: '60%'}).append(tweet);
          //var by        = $('<p></p>').text(json.social.by); //
          var twIcon    = $('<img>').attr({src: './assets/img/twitter.jpg', alt: 'Twitter'});
          var twLink    = $('<a></a>').attr('href', json.social.twitter).append(twIcon);
          var twFrame   = $('<div></div>').addClass('socialMed').append(twLink); //
          var fbIcon    = $('<img>').attr({src: './assets/img/facebook.jpg', alt: 'Facebook'});
          var fbLink    = $('<a></a>').attr('href', json.social.facebook).append(fbIcon);
          var fbFrame   = $('<div></div>').addClass('socialMed').append(fbLink); //
          var btnLink   = $('<a></a>').attr({href: 'http://www.rit.edu/admission.html', id: 'btnLink'}).text(json.quickLinks[0].title);
          var linkBtn   = $('<div></div>').attr('id', 'button').append(btnLink);
          var link      = $('<div></div>').attr('id', 'applyField').append(linkBtn);
          var copyright = json.copyright.html;
          // var news      = json.news;

          $('footer').append(title, animator, '<br>', twFrame, fbFrame, link, copyright);
      });


    /** SMOOTH PAGE SCROLLING EFFECT **/
      $(window).on('scroll', function(){
        ( $(window).scrollTop() > 50 ) ? ( $('header').addClass('active') ) :
              $('header').removeClass('active');
              $('header').css('transition', 'all 0.3s ease');
      });

    /** MOBILE EFFECTS (Not working yet. SERVER ERROR ???) **/
        // if ( $(window).width() <= 640 ) {
        // // MOBILE TOGGLING
        //   $('.header-items:not(:first-child)').hide();
        //   $('#banner').click(function() {
        //     $('.header-items:not(:first-child)').slideToggle();
        //   });
        // }

}); // end of document.ready()



/** AJAX function for rendering page content from RIT's IST API **/

    function renderData(reqMethod, path, elem){
        return $.ajax({
          type: reqMethod,
          url: 'proxy.php',
          dataType: 'json',
          data: path,
          cache: false,
          async: true,
          beforeSend: function() {
            //show loader while page is loading
            $('<body></body>').append( $('<img>').attr({src: './assets/img/ist_logo.png', id: 'spinner'}) );
          }
        }).always(function(){
          $('#spinner').remove();
        }).fail(function(){
         // Handle error
       }); // end of 'fail' block
    } // end of renderData()



/** Custom JavaScript **/

    //TO-DO: Fix the function (still not working)
    function setCurrentTab(selectedTabContent, elem, bkgColor, txtColor) {
        var i, tab, content;

        tab = document.getElementsByTagName('button');
        for(i=0; i<tab.length; i++) {
          tab[i].style.backgroundColor = '';
          tab[i].style.color = '';
        }

        content = document.getElementsByClassName('content-cell');
        for(i=0; i<content.length; i++) {
          content[i].style.display = 'none';
        }

        document.getElementById(selectedTabContent).style.display = 'block';
        elem.style.backgroundColor = bkgColor;
        elem.style.color = txtColor;
    }

    document.getElementById('current').click();
