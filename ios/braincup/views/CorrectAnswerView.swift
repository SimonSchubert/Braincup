//
//  MainMenuView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct CorrectAnswerView: View {
    var hint: String?
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Image("success")
                if(hint != nil) {
                    Text(hint!).font(.body).padding(.top, 16)
                }
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle()) 
    }
}
