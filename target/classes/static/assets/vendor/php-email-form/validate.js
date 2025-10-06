/**
* PHP Email Form Validation - v3.9
* URL: https://bootstrapmade.com/php-email-form/
* Author: BootstrapMade.com
*/
(function () {
  "use strict";

  let forms = document.querySelectorAll('.php-email-form');
  let isSubmitting = false; // 全局提交狀態

  forms.forEach( function(e) {
    e.addEventListener('submit', function(event) {
      event.preventDefault();

      let thisForm = this;
      
      // 防止重複提交 - 多重檢查
      if (thisForm.classList.contains('submitting') || isSubmitting) {
        return false;
      }
      
      // 設置提交狀態
      thisForm.classList.add('submitting');
      isSubmitting = true;
      
      // 禁用提交按鈕
      let submitBtn = thisForm.querySelector('button[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.style.opacity = '0.6';
      }

      let action = thisForm.getAttribute('action');
      let recaptcha = thisForm.getAttribute('data-recaptcha-site-key');
      
      if( ! action ) {
        displayError(thisForm, 'The form action property is not set!');
        resetSubmitState(thisForm);
        return;
      }
      thisForm.querySelector('.loading').classList.add('d-block');
      thisForm.querySelector('.error-message').classList.remove('d-block');
      thisForm.querySelector('.sent-message').classList.remove('d-block');

      let formData = new FormData( thisForm );

      if ( recaptcha ) {
        if(typeof grecaptcha !== "undefined" ) {
          grecaptcha.ready(function() {
            try {
              grecaptcha.execute(recaptcha, {action: 'php_email_form_submit'})
              .then(token => {
                formData.set('recaptcha-response', token);
                php_email_form_submit(thisForm, action, formData);
              })
            } catch(error) {
              displayError(thisForm, error);
              resetSubmitState(thisForm);
            }
          });
        } else {
          displayError(thisForm, 'The reCaptcha javascript API url is not loaded!');
          resetSubmitState(thisForm);
        }
      } else {
        php_email_form_submit(thisForm, action, formData);
      }
    });
  });

  function php_email_form_submit(thisForm, action, formData) {
    // Check if already submitting via fetch
    if (thisForm.dataset.fetchSubmitting === 'true') {
      return;
    }
    
    // Mark as submitting via fetch
    thisForm.dataset.fetchSubmitting = 'true';
    
    // 添加唯一的請求 ID 來防止重複提交
    const requestId = Date.now() + '-' + Math.random().toString(36).substr(2, 9);
    formData.set('requestId', requestId);
    
    console.log('Submitting form with request ID:', requestId);
    
    fetch(action, {
      method: 'POST',
      body: formData,
      headers: {'X-Requested-With': 'XMLHttpRequest'}
    })
    .then(response => {
      if( response.ok ) {
        return response.text();
      } else {
        throw new Error(`${response.status} ${response.statusText} ${response.url}`); 
      }
    })
    .then(data => {
      console.log('Form submission response:', data);
      thisForm.querySelector('.loading').classList.remove('d-block');
      if (data.trim() == 'OK') {
        thisForm.querySelector('.sent-message').classList.add('d-block');
        thisForm.reset(); 
      } else {
        throw new Error(data ? data : 'Form submission failed and no error message returned from: ' + action); 
      }
      resetSubmitState(thisForm);
    })
    .catch((error) => {
      console.error('Form submission error:', error);
      displayError(thisForm, error);
      resetSubmitState(thisForm);
    });
  }

  function displayError(thisForm, error) {
    thisForm.querySelector('.loading').classList.remove('d-block');
    thisForm.querySelector('.error-message').innerHTML = error;
    thisForm.querySelector('.error-message').classList.add('d-block');
    resetSubmitState(thisForm);
  }

  function resetSubmitState(thisForm) {
    // 清除所有提交狀態
    isSubmitting = false;
    thisForm.classList.remove('submitting');
    thisForm.dataset.fetchSubmitting = 'false';
    
    // 重新啟用按鈕
    const submitBtn = thisForm.querySelector('button[type="submit"]');
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.style.opacity = '1';
    }
  }

})();
