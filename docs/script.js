window.addEventListener('scroll', () => {
    const section = document.querySelector('.section1');
    const image = document.querySelector('.backImg');

    const sectionTop = section.offsetTop;
    const scrollY = window.scrollY;
    const speed = 0.4;

    const offset = scrollY - sectionTop;
    image.style.transform = `translateY(${offset * speed}px)`;
});
console.log("script running");
//window.addEventListener('scroll', function() {
//    const parallax = document.querySelector('.backImg');
//    let scrollPosition = window.pageYOffset;

//    parallax.style.transform = 'translateY(' + scrollPosition * .6 + 'px)';
//}); 