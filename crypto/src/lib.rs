#![no_std]
#![feature(lang_items)]

#[cfg(test)]
mod tests;

mod exports;

#[cfg(not(test))]
mod lang_item;
