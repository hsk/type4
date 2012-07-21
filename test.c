void aaa(int *a) {
  int c = a[100];
  printInt(c);
}

int main() {
  int *a = malloc(100);
  a[100] = 5;
  aaa(a); 
  return 0;
}
