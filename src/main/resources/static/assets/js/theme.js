// 브라우저가 HTML을 로드하기 전에 테마 설정
(function () {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-bs-theme', savedTheme);
})();